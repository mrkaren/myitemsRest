package com.example.myitemsrest;

import java.util.*;

public class LambdaExample {

    public static void main(String[] args) {

        List<String> names = new ArrayList<>(List.of("poxos", "petros", "martiros", "aram"));

        names.sort(Comparator.comparingInt(String::length));

        names.forEach(System.out::println);

//        Set<String> nameSet = names.stream()
//                .filter(e -> e.startsWith("p"))
//                .map(String::toUpperCase)
//                .collect(Collectors.toSet());
//
//        nameSet.forEach(System.out::println);

    }

}
