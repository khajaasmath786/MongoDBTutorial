package com.mongodb;

import java.util.List;
import java.util.ArrayList;
import java.time.chrono.IsoChronology;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.Period;

public class Person {
  
    public enum Sex {
        MALE, FEMALE
    }
  
    String name; 
    LocalDate birthday;
    Sex gender;
    String emailAddress;
  
    Person(String nameArg, LocalDate birthdayArg,
        Sex genderArg, String emailArg) {
        name = nameArg;
        birthday = birthdayArg;
        gender = genderArg;
        emailAddress = emailArg;
    }  

    public int getAge() {
        return birthday
            .until(IsoChronology.INSTANCE.dateNow())
            .getYears();
    }

    public void printPerson() {
      System.out.println(name + ", " + this.getAge());
    }
    
    public Sex getGender() {
        return gender;
    }
    
    public String getName() {
        return name;
    }
   
    public String getEmailAddress() {
        return emailAddress;
    }
    
    public LocalDate getBirthday() {
        return birthday;
    }
    
    public static int compareByAge(Person a, Person b) {
        return a.birthday.compareTo(b.birthday);
    }

    public static void compareByNames(String birthday, String  name) {
        System.out.println(birthday+name);
    }
    
    public static void transform(String s1,String s2,
    		StringFunction f) {
    		f.applyFunction(s1,s2);
    		}
    
    public static List<Person> createRoster() {
        
        List<Person> roster = new ArrayList<>();
        roster.add(
            new Person(
            "Fred",
            IsoChronology.INSTANCE.date(1980, 6, 20),
            Person.Sex.MALE,
            "fred@example.com"));
        roster.add(
            new Person(
            "Jane",
            IsoChronology.INSTANCE.date(1990, 7, 15),
            Person.Sex.FEMALE, "jane@example.com"));
        roster.add(
            new Person(
            "George",
            IsoChronology.INSTANCE.date(1991, 8, 13),
            Person.Sex.MALE, "george@example.com"));
        roster.add(
            new Person(
            "Bob",
            IsoChronology.INSTANCE.date(2000, 9, 12),
            Person.Sex.MALE, "bob@example.com"));
        
        return roster;
    }
    
/*    Where Can Lambdas Be Used?
    		• Find any variable or parameter that expects an
    		interface that has one method • Technically 1 abstract method, but in Java 7 there was no
    		distinction between a 1-method interface and a 1-abstractmethod
    		interface. These 1-method interfaces are called
    		“functional interfaces” or “SAM (Single Abstract Method)
    		interfaces”.
    		– public interface Blah { String foo(String someString); }
    		
    		
    		• Code that uses interface is the same
    		– public void someMethod(Blah b) { … b.foo(…) …}
    		• Code that uses the interface must still know the real method
    		name of the interface
    		
    		
    		• Code that calls the method that expects the
    		interface can supply lambda
    		– String result = someMethod(s -> s.toUpperCase() + "!");
    
    
    • ClassName::staticMethodName
    – E.g., Math::cos, Arrays::sort or String::valueOf
    • Another way of saying this is that if the function you want
    to describe already has a name, you don’t have to write a
    lambda for it, but can instead just use the method name.
    – The signature of the method you refer to must match
    signature of the method in functional (SAM) interface to
    which it is assigned
    */
    
}
