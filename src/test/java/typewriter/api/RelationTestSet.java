/*
 * Copyright (C) 2023 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package typewriter.api;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import kiss.Signal;
import typewriter.api.model.DerivableModel;

public interface RelationTestSet extends Testable {

    @Test
    default void saveRelation() {
        Employee emp1 = new Employee("one");
        Employee emp2 = new Employee("two");
        Employee emp3 = new Employee("three");
        Department dep1 = new Department("Red");
        Department dep2 = new Department("Blue");

        emp1.department = dep1;
        emp2.department = dep1;
        emp3.department = dep2;
        dep1.employees.addAll(List.of(emp1, emp2));
        dep2.employees.addAll(List.of(emp3));

        QueryExecutor<Employee, Signal<Employee>, ?, ?> emp = createEmptyDB(Employee.class);
        emp.updateAll(emp1, emp2, emp3);

        QueryExecutor<Department, Signal<Department>, ?, ?> dept = createEmptyDB(Department.class);
        List<Department> list = dept.findAll().toList();
        assert list.size() == 2;
        assert list.get(0).name.equals("Red");
        assert list.get(1).name.equals("Blue");
    }

    class Employee extends DerivableModel {

        public String name;

        public Department department;

        Employee(String name) {
            this.name = name;
        }

    }

    class Department extends DerivableModel {

        public String name;

        public List<Employee> employees = new ArrayList();

        Department(String name) {
            this.name = name;
        }
    }
}
