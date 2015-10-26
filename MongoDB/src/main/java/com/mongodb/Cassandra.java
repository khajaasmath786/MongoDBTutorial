package com.mongodb;

package com.jdpower.cdp.scheduler.dataload.storm.topology;

import com.jdpower.cdp.mdm.service.Mdm5Service;
import com.jdpower.cdp.scheduler.dataload.storm.aggregator.EnrichRecordAggregator;
import com.jdpower.cdp.scheduler.dataload.storm.aggregator.RawRecordAggregator;
import com.jdpower.cdp.scheduler.dataload.storm.aggregator.UIDAggregator;
import com.jdpower.cdp.scheduler.dataload.storm.function.CleaningRuleOperator;
import com.jdpower.cdp.scheduler.dataload.storm.function.ConstructRawRecord;
import com.jdpower.cdp.scheduler.dataload.storm.function.DataInitializer;
import com.jdpower.cdp.scheduler.dataload.storm.function.DependenciesProvider;
import com.jdpower.cdp.scheduler.dataload.storm.function.EnrichJobInitializer;
import com.jdpower.cdp.scheduler.dataload.storm.function.ErrorMessageHandler;
import com.jdpower.cdp.scheduler.dataload.storm.function.ImportDataProvider;
import com.jdpower.cdp.scheduler.dataload.storm.function.ImportDemux;
import com.jdpower.cdp.scheduler.dataload.storm.function.SaveToCassandra;
import com.jdpower.cdp.scheduler.dataload.storm.function.SaveToSolr;
import com.jdpower.cdp.scheduler.dataload.storm.function.StructureProvider;
import com.jdpower.cdp.scheduler.dataload.storm.function.TriggerDataProvider;
import com.jdpower.cdp.scheduler.dataload.storm.function.TriggerDemux;
import com.jdpower.cdp.scheduler.service.CloudSearchService;
import com.jdpower.cdp.scheduler.service.CryptoService;
import com.jdpower.cdp.scheduler.service.JobService;

import backtype.storm.LocalDRPC;
import backtype.storm.generated.StormTopology;
import backtype.storm.tuple.Fields;
import storm.trident.Stream;
import storm.trident.TridentTopology;

public class DataLoadTridentTopology {

    public static StormTopology buildDRPC(Mdm5Service service, CryptoService cryptoService, CloudSearchService cloudSearchService, JobService jobService, LocalDRPC client) {
        TridentTopology topology = new TridentTopology();
        
        createFileInputTopology(topology, client, service, cryptoService, cloudSearchService, jobService);
        createEnrichmentTopology(topology, client, service, cryptoService, cloudSearchService);


        return topology.build();
    }

    public static void createFileInputTopology(TridentTopology topology, LocalDRPC client, Mdm5Service service, CryptoService cryptoService, CloudSearchService cloudSearchService, JobService jobService) {
        Stream stream = topology.newDRPCStream("dataload", client);

        stream = stream.each(new Fields("args"), new DataInitializer(), new Fields("rawInputMap"))
                .each(new Fields("rawInputMap"), new ConstructRawRecord(service, cryptoService), new Fields("rawResponseStatus", "surveyId", "rawMap", "loadParams"))
                .groupBy(new Fields("rawResponseStatus", "surveyId", "loadParams"))
                .aggregate(new Fields("rawResponseStatus", "surveyId", "rawMap"), new RawRecordAggregator(), new Fields("dataPayLoads"));

        stream.each(new Fields("dataPayLoads"), new ErrorMessageHandler(), new Fields());

        /*stream = stream.each(new Fields("surveyId", "values"), new SaveToCassandra(service), new Fields("type1", "value"))
            .each(new Fields("type1", "surveyId", "value"), new FetchStudy(service), new Fields("type2", "studyId"))
            .each(new Fields("type2", "surveyId", "studyId", "value"), new ConstructRecord(service), new Fields("type3", "record"))
            .groupBy(new Fields("type3", "surveyId", "studyId"))
            .aggregate(new Fields("type3", "surveyId", "studyId", "record"), new RecordAggregator(), new Fields("values")).toStream();*/

        stream = stream.each(new Fields("surveyId", "dataPayLoads"), new SaveToCassandra(service), new Fields("uidStatus", "uid"))
                /*.each(new Fields("type1", "surveyId", "value"), new FetchStudy(service), new Fields("type2", "studyId"))
                .each(new Fields("type2", "surveyId", "studyId", "value"), new ConstructRecord(service), new Fields("type3", "record"))*/
                .groupBy(new Fields("surveyId", "uidStatus", "uid", "loadParams"))
                .aggregate(new Fields("surveyId", "uidStatus", "uid"), new UIDAggregator(), new Fields("dataPayLoads"));

        stream.each(new Fields("dataPayLoads"), new ErrorMessageHandler(), new Fields());

        stream.each(new Fields("surveyId", "dataPayLoads", "loadParams"), new EnrichJobInitializer(client, service, jobService), new Fields());

        /*stream.each(new Fields("type3", "surveyId", "studyId", "values"), new SaveToSolr(service), new Fields("error", "message"))
            .groupBy(new Fields("error"))
            .aggregate(new Fields("error", "message"), new ErrorMessageAggregator(), new Fields("values"))
            .each(new Fields("values"), new ErrorMessageHandler(), new Fields());*/
    }

    public static void createEnrichmentTopology(TridentTopology topology, LocalDRPC client, Mdm5Service service, CryptoService cryptoService, CloudSearchService cloudSearchService) {
		Stream stream = topology.newDRPCStream("enrichment", client);
		// Stream stream=(String enrichment,LocalDRPC client) -> topology.newDRPCStream(enrichment, client);
		// StructureProvider structureProvider= (Mdm5Service service,CloudSearchService cloudSearchService,CryptoService cryptoService) -> 

		// stream.each(new Fields("args"), new DummyLogger(), new Fields());
		

		stream = stream.each(new Fields("args"), new StructureProvider(service, cloudSearchService, cryptoService), new Fields("uid", "surveyId", "triggerId", "baseStructure", "loadParams"))
			.each(new Fields("uid", "triggerId"), new DependenciesProvider(service), new Fields("triggers", "imports"));

        stream = stream.each(new Fields("surveyId", "baseStructure"), new CleaningRuleOperator(service), new Fields("surveyCleanedRecord"));

		// stream.each(new Fields("uid", "baseStructure", "triggers", "imports"), new DummyLogger(), new Fields());

		stream = stream.each(new Fields("triggers"), new TriggerDemux(), new Fields("trigger"))
            .each(new Fields("trigger", "baseStructure"), new TriggerDataProvider(), new Fields("triggerData"))

			.each(new Fields("imports"), new ImportDemux(), new Fields("import"))
			.each(new Fields("surveyId", "import", "baseStructure"), new ImportDataProvider(service, cloudSearchService), new Fields("importData"))

			.groupBy(new Fields("uid", "surveyId", "triggerId", "loadParams"))
			.aggregate(new Fields("surveyCleanedRecord", "triggerData", "importData"), new EnrichRecordAggregator(), new Fields("record"));

        // stream.each(new Fields("record"), new DummyLogger(), new Fields());

		stream = stream.each(new Fields("triggerId", "record"), new CleaningRuleOperator(service), new Fields("cleanedRecord"))
		    .each(new Fields("surveyId", "triggerId", "cleanedRecord", "loadParams"), new SaveToSolr(service), new Fields());
    }
}
