# Персистентные структуры данных

## Формулировка задания

Реализовать библиотеку на Java со следующими структурами данных в persistent-вариантах:

* Массив
* Двусвязный список
* Ассоциативный массив

#### Базовые требования

- [x] PArray -- персистентный массив
- [x] PDoublyLinkedList -- персистентный двусвязный список
- [x] PHashMap -- персистентный ассоциативный массив

- [x] Для всех структур реализован единый API

#### Дополнительные требования

- [x] Реализовать структуры данных на основе алгоритма path copying
- [x] Обеспечить произвольную вложенность данных, не отказываясь от типизации посредством generic
- [x] Реализовать универсальный undo-redo механизм для перечисленных структур с поддержкой каскадности (для вложенных структур)

## Календарный план

| Дата       | Реализованная функциональность                 |
| ---------- | ---------------------------------------------- |
| 07.12.2024 | Реализация базовых требований                  |
| 21.12.2024 | Реализация дополнительных требований |

## Ответственные

| ФИО                         | Группа  |
| --------------------------- | ------- |
| Лапочкин Дмитрий Алексеевич | 24225.1 |

## Источники

### Персистентный вектор (массив)
* [**Understanding Clojure's Persistent Vectors, pt. 1**](https://hypirion.com/musings/understanding-persistent-vector-pt-1)
* [**Understanding Clojure's Persistent Vectors, pt. 2**](https://hypirion.com/musings/understanding-persistent-vector-pt-2)
* [Understanding Clojure's Persistent Vectors, pt. 3](https://hypirion.com/musings/understanding-persistent-vector-pt-3)
* [Understanding Clojure's Transients](https://hypirion.com/musings/understanding-clojure-transients)
* [Persistent Vector Performance Summarised](https://hypirion.com/musings/persistent-vector-performance-summarised)
* [Persistent vectors, Part 1 -- The landscape](https://dmiller.github.io/clojure-clr-next/general/2023/02/12/PersistentVector-part-1.html)
* [**Persistent vectors, Part 2 -- Immutability and persistence**](https://dmiller.github.io/clojure-clr-next/general/2023/02/12/PersistentVector-part-2.html)

### Персистентный ассоциативный массив
* [**PersistentHashMap, Part 1 -- Making a hash of things**](https://dmiller.github.io/clojure-clr-next/general/2024/07/02/persistent-hash-map-part-1.html)
* [PersistentHashMap, part 2 -- The root](https://dmiller.github.io/clojure-clr-next/general/2024/07/02/persistent-hash-map-part-2.html)
* [PersistentHashMap, part 3 -- The guts](https://dmiller.github.io/clojure-clr-next/general/2024/07/02/persistent-hash-map-part-3.html)
* [PersistentHashMap, part 4 -- Other matters](https://dmiller.github.io/clojure-clr-next/general/2024/07/02/persistent-hash-map-part-4.html)
* [PersistentHashMap, part 5 -- At a loss](https://dmiller.github.io/clojure-clr-next/general/2024/07/02/persistent-hash-map-part-5.html)
* [**Hash table**](https://en.wikipedia.org/wiki/Hash_table)

### Undo-Redo
* [Undo-redo and its effects on your architecture](https://www.philgiese.com/post/undo-redo-architecture)

### Примеры реализации (GitHub)
* [Persistent vector performance measurements and analysis](https://github.com/hypirion/pvec-perf/tree/master)
* [Clojure's PersistentVector](https://github.com/clojure/clojure/blob/0b73494c3c855e54b1da591eeb687f24f608f346/src/jvm/clojure/lang/PersistentVector.java)
* [Clojure's PersistentHashMap](https://github.com/clojure/clojure/blob/0b73494c3c855e54b1da591eeb687f24f608f346/src/jvm/clojure/lang/PersistentHashMap.java)

## Структура проекта

```
└── 📁src
    └── 📁main
        └── 📁java
            └── 📁pds
                └── PArray.java
                └── PDoublyLinkedList.java
                └── PHashMap.java
                └── 📁SubClasses
                    └── 📁PDoublyLinkedListClasses
                        └── ListNode.java
                    └── 📁PHashMapClasses
                        └── MapNode.java
                    └── 📁TrieClasses
                        └── Head.java
                        └── HeadList.java
                        └── Node.java
                    └── 📁UndoRedoClasses
                        └── UndoRedoDataStructure.java
                        └── UndoRedoStack.java
        └── 📁resources
    └── 📁test
        └── 📁java
            └── 📁pds
                └── TestPArray.java
                └── TestPDoublyLinkedList.java
                └── TestPHashMap.java
        ├── resources
```