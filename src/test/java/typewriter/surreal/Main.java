/*
 * Copyright (C) 2025 The TYPEWRITER Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package typewriter.surreal;

import java.util.List;

import com.surrealdb.driver.SyncSurrealDriver;

public class Main {

    public static void main(String[] args) throws Exception {
        SurrealManager.with().logLevel("trace").autoClose(true).establish().to(connection -> {
            SyncSurrealDriver driver = new SyncSurrealDriver(connection);

            driver.use("test", "test"); // namespace & database

            Person tobie = driver.create("aa", new Person("Founder & CEO", "Tobie", "Morgan Hitchcock", true));

            List<Person> people = driver.select("aa", Person.class);

            System.out.println();
            System.out.println("Tobie = " + tobie);
            System.out.println();
            System.out.println(people.size() + " = " + people);
            System.out.println();

            connection.close();
        });
        Thread.sleep(5000);
    }

    static class Person {
        String id;

        String title;

        String firstName;

        String lastName;

        boolean marketing;

        public Person(String title, String firstName, String lastName, boolean marketing) {
            this.title = title;
            this.firstName = firstName;
            this.lastName = lastName;
            this.marketing = marketing;
        }

        @Override
        public String toString() {
            return "Person{" + "id='" + id + '\'' + ", title='" + title + '\'' + ", firstName='" + firstName + '\'' + ", lastName='" + lastName + '\'' + ", marketing=" + marketing + '}';
        }
    }
}