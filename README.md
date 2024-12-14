# Персистентные структуры данных

## Формулировка задания

Реализовать библиотеку на Java со следующими структурами данных в persistent-вариантах:

* Массив
* Двусвязный список
* Ассоциативный массив

#### Базовые требования

- [x] PArray -- персистентный массив
- [ ] PList -- персистентный двусвязный список
- [ ] PMap -- персистентный ассоциативный массив

- [x] Для всех структур реализован единый API

#### Дополнительные требования

- [x] Реализовать структры данных на основе алгоритма Path Copying
- [x] Обеспечить произвольную вложенность данных (по аналогии с динамическими языками), не отказываясь при этом полностью от типизации посредством generic/template
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

## Структура проекта

├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── pds/
│   │   │       ├── PersistentClasses/
│   │   │       │   └── PArray.java
│   │   │       └── UtilClasses/
│   │   │           ├── Head.java
│   │   │           ├── Node.java
│   │   │           └── VersionStack.java
│   │   └── resources/
│   └── test/
│       ├── java/
│       │   └── pds/
│       │       └── PArrayTest.java
│       └── resources/

## API

## Источники

### Статьи
* [Персистентные структуры данных](https://neerc.ifmo.ru/wiki/index.php?title=%D0%9F%D0%B5%D1%80%D1%81%D0%B8%D1%81%D1%82%D0%B5%D0%BD%D1%82%D0%BD%D1%8B%D0%B5_%D1%81%D1%82%D1%80%D1%83%D0%BA%D1%82%D1%83%D1%80%D1%8B_%D0%B4%D0%B0%D0%BD%D0%BD%D1%8B%D1%85)
* [Understanding Clojure's Persistent Vectors, pt. 1](https://hypirion.com/musings/understanding-persistent-vector-pt-1)
* [Understanding Clojure's Persistent Vectors, pt. 2](https://hypirion.com/musings/understanding-persistent-vector-pt-2)
* [Understanding Clojure's Persistent Vectors, pt. 3](https://hypirion.com/musings/understanding-persistent-vector-pt-3)
* [Understanding Clojure's Transients](https://hypirion.com/musings/understanding-clojure-transients)
* [Persistent Vector Performance Summarised](https://hypirion.com/musings/persistent-vector-performance-summarised)
* [Persistent vectors, Part 1 -- The landscape](https://dmiller.github.io/clojure-clr-next/general/2023/02/12/PersistentVector-part-1.html)
* [Persistent vectors, Part 2 -- Immutability and persistence](https://dmiller.github.io/clojure-clr-next/general/2023/02/12/PersistentVector-part-2.html)
* [Undo-redo and its effects on your architecture](https://www.philgiese.com/post/undo-redo-architecture)

### GitHub
* [Persistent vector performance measurements and analysis ](https://github.com/hypirion/pvec-perf/tree/master)
* [Реализация PersistentVector в Clojure](https://github.com/clojure/clojure/blob/0b73494c3c855e54b1da591eeb687f24f608f346/src/jvm/clojure/lang/PersistentVector.java)
