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
- [x] Обеспечить произвольную вложенность данных, не отказываясь от типизации посредством generic/template
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
                    └── 📁CopyPathClasses
                        └── Head.java
                        └── HeadList.java
                        └── Node.java
                    └── 📁PDoublyLinkedListClasses
                        └── ListNode.java
                    └── 📁PHashMapClasses
                        └── MapNode.java
                    └── 📁UndoRedoClasses
                        └── UndoRedoDataStructure.java
                        └── UndoRedoStack.java
    └── 📁test
        └── 📁java
            └── 📁pds
                └── PArrayTests.java
                └── PDoublyLinkedListTests.java
                └── PHashMapTests.java
```

## API

### UndoRedoDataStructure

Структура данных с поддержкой механизма undo-redo.

###### `public void undo()`

Возвращает к предыдущей версии структуры данных.

###### `public void redo()`

Возвращает к версии структуры данных до выполнения undo.

###### `public String toString()`

Преобразует структуру данных в строковое представление.

 * **Returns:** строковое представление структуры данных

###### `public int currentVersion()`

Возвращает номер текущей версии структуры данных.

 * **Returns:** номер текущей версии

###### `public int countVersions()`

Возвращает количество версий структуры данных.

 * **Returns:** количество версий структуры данных

### PArray<E> extends UndoRedoDataStructure

Персистентный массив.

 * **Parameters:** `<E>` — тип элементов в массиве

###### `public PArray(int height, int bitsPerNode)`

Конструктор класса.

 * **Parameters:**
   * `height` — высота (глубина) двоичного дерева персистентного массива
   * `bitsPerNode` — число бит на каждую ноду двоичного дерева персистентного массива

###### `public PArray()`

Конструктор класса со значениями по умолчанию (heigth = 3, bitsPerNode = 2).

###### `public PArray(PArray<E> other)`

Конструктор класса.

 * **Parameters:** `other` — объект класса PArray

###### `public List<Object> toList()`

Преобразует персистентный массив в список.

 * **Returns:** список, содержащий элементы персистентного массива

###### `public boolean contains(Object value)`

Возвращает true, если элемент присутствует в персистентом массиве.

 * **Parameters:** `value` — элемент
 * **Returns:** true, если элемент присутствует в персистентом массиве; false, иначе

###### `public int indexOf(Object value)`

Возвращает индекс элемента в персистентном массиве.

 * **Parameters:** `value` — элемент
 * **Returns:** индекс элемента в персистентом массиве, если присутствует; -1, иначе

###### `public int size()`

Возвращает количество элементов в персистентном массиве.

 * **Returns:** количество элементов в персистентном массиве

###### `public boolean isEmpty()`

Возвращает true, если персистентный массив пустой.

 * **Returns:** true, если персистентный массив пустой; false, если иначе

###### `public boolean isFull()`

Возвращает true, если персистентный массив полный.

 * **Returns:** true, если персистентный массив полный; false, если иначе

###### `public E get(int index)`

Возвращает из персистентного массива элемент по индексу.

 * **Parameters:** `index` — индекс элемента в персистентном массиве
 * **Returns:** элемент из персистентного массива

###### `public boolean add(E value)`

Добавляет элемент в конец персистентного массива.

 * **Parameters:** `value` — добавляемый элемент
 * **Returns:** true, если элемент был добавлен в персистентный массив

###### `public void add(int index, E value)`

Добавляет элемент в персистентный массив по индексу.

 * **Parameters:**
   * `index` — индекс в персистентном массиве
   * `value` — добавляемый элемент

###### `public boolean addAll(List<E> values)`

Добавляет все элементы из списка в конец персистентного массива.

 * **Parameters:** `values` — список, содержащий добавляемые элементы
 * **Returns:** true, если все элементы из списка были добавлены в персистентный массив

###### `public E set(int index, E value)`

Заменяет элемент из персистентного массива на новый.

 * **Parameters:**
   * `index` — индекс элемента
   * `value` — новый элемент
 * **Returns:** элемент до замены

###### `public E remove(int index)`

Удаляет элемент из персистентного массива по индексу.

 * **Parameters:** `index` — индекс элемента
 * **Returns:** удаленный из персистентного массива элемент

###### `public void clear()`

Удаляет все элементы из персистентного массива.

###### `public int maxSize()`

Возвращает максимальный размер персистентного массива.

 * **Returns:** максимальный размер персистентного массива

###### `public int getBitsPerNode()`

Возвращает число бит на каждую ноду двоичного дерева персистентного массива.

 * **Returns:** число бит на каждую ноду двоичного дерева

###### `public int getheight()`

Возвращает высоту (глубину) двоичного дерева персистентного массива.

 * **Returns:** высота двоичного дерева

### PDoublyLinkedList<E> extends UndoRedoDataStructure

Персистентный двусвязный список

 * **Parameters:** `<E>` — тип элементов в списке

###### `public PDoublyLinkedList(int height, int bitsPerNode)`

Конструктор класса.

 * **Parameters:**
   * `height` — высота (глубина) двоичного дерева персистентного списка
   * `bitsPerNode` — число бит на каждую ноду двоичного дерева персистентного списка

###### `public PDoublyLinkedList()`

Конструктор класса со значениями по умолчанию (heigth = 3, bitsPerNode = 2).

###### `public PDoublyLinkedList(PDoublyLinkedList<E> other)`

Конструктор класса.

 * **Parameters:** `other` — объект класса PDoublyLinkedList

###### `public List<Object> toList()`

Преобразует персистентный список в список.

 * **Returns:** список, содержащий элементы персистентного списка

###### `public boolean contains(Object value)`

Возвращает true, если элемент присутствует в персистентом списке.

 * **Parameters:** `value` — элемент
 * **Returns:** true, если элемент присутствует в персистентом списке; false, иначе

###### `public int indexOf(Object value)`

Возвращает индекс элемента в персистентном списке.

 * **Parameters:** `value` — элемент
 * **Returns:** индекс элемента в персистентом списке, если присутствует; -1, иначе

###### `public int size()`

Возвращает количество элементов в персистентном списке.

 * **Returns:** количество элементов в персистентном списке

###### `public boolean isEmpty()`

Возврщает true, если персистентный список пустой.

 * **Returns:** true, если персистентный список пустой; false, если иначе

###### `public boolean isFull()`

Возвращает true, если персистентный список полный.

 * **Returns:** true, если персистентный список полный; false, если иначе

###### `public E get(int index)`

Возвращает из персистентного списка элемент по индексу.

 * **Parameters:** `index` — индекс элемента в персистентном списке
 * **Returns:** элемент из персистентного списка

###### `public boolean add(E value)`

Добавляет элемент в конец персистентного списка.

 * **Parameters:** `value` — добавляемый элемент
 * **Returns:** true, если элемент был добавлен в персистентный список

###### `public void add(int index, E value)`

Добавляет элемент в персистентный список по индексу.

 * **Parameters:**
   * `index` — индекс в персистентном списке
   * `value` — добавляемый элемент

###### `public boolean addAll(List<E> values)`

Добавляет все элементы из списка в конец персистентного списка.

 * **Parameters:** `values` — список, содержащий добавляемые элементы
 * **Returns:** true, если все элементы из списка были добавлены в персистентный список

###### `public E set(int index, E value)`

Заменяет элемент из персистентного списка на новый.

 * **Parameters:**
   * `index` — индекс элемента
   * `value` — новый элемент
 * **Returns:** элемент до замены

###### `public E remove(int index)`

Удаляет элемент из персистентного списка по индексу.

 * **Parameters:** `index` — индекс элемента
 * **Returns:** удаленный из персистентного списка элемент

###### `public void clear()`

Удаляет все элементы из персистентного списка.

###### `public int maxSize()`

Возвращает максимальный размер персистентного списка.

 * **Returns:** максимальный размер персистентного списка

###### `public int getBitsPerNode()`

Возвращает число бит на каждую ноду двоичного дерева персистентного списка.

 * **Returns:** число бит на каждую ноду двоичного дерева

###### `public int getheight()`

Возвращает высоту (глубину) двоичного дерева персистентного списка.

 * **Returns:** высота двоичного дерева

###### `public List<Object> toArray()`

Преобразует персистентный список в список, содержащий элементы в порядке их хранения в двоичном дереве.

 * **Returns:** список, содержащий элементы персистентного списка


### PHashMap<K, V> extends UndoRedoDataStructure

Персистентный ассоциативный массив на основе хэш-таблицы.

 * **Parameters:**
   * `<K>` — тип ключей ассоциативного массива
   * `<V>` — тип значений ассоциативного массива

###### `public PHashMap(int width)`

Конструктор класса.

 * **Parameters:** `width` — размер хэш-таблицы

###### `public PHashMap()`

Конструктор класса со значением хэш-таблицы по умолчанию (width = 32).

###### `public PHashMap(PHashMap<K, V> other)`

Конструктор класса.

 * **Parameters:** `other` — объект класса PHashTable

###### `@Override public void undo()`

Возвращает к предыдущей версии структуры данных.

###### `@Override public void redo()`

Возврат к версии структуры данных до выполнения {@link #undo() undo}.

###### `public List<Object> toList()`

Преобразует персистентный ассоциатный массив в список.

 * **Returns:** список, содержащий элементы персистентного ассоциативного массива

###### `public Set<MapNode<K, V>> entrySet()`

Возвращает список всех пар "ключ-значение" ассоциативного массива.

 * **Returns:** список всех пар "ключ-значение" ассоциативного массива

###### `public Set<K> keySet()`

Возвращает список всех ключей в ассоциативном массиве.

 * **Returns:** список всех ключей в ассоциативном массиве

###### `public List<V> values()`

Возвращает список всех значений в ассоциативном массиве.

 * **Returns:** список всех значений в ассоциативном массиве

###### `public boolean containsKey(K key)`

Возвращает true если ассоциативный массив содержит ключ.

 * **Parameters:** `key` — ключ
 * **Returns:** true, если ассоциативный массив содержит ключ; false, иначе

###### `public boolean containsValue(V value)`

Возвращает true если ассоциативный массив содержит значение.

 * **Parameters:** `value` — значение
 * **Returns:** true, если ассоциативный массив содержит значение; false, иначе

###### `public int size()`

Возвращает число пар "ключ-значение" в ассоциативном массиве.

 * **Returns:** число пар "ключ-значение" в ассоциативном массиве

###### `public boolean isEmpty()`

Возвращает true, если ассоциативный массив пустой.

 * **Returns:** true, если ассоциативный массив пустой; false, если иначе

###### `public V get(K key)`

Возвращает из ассоциативного массива значение по ключу.

 * **Parameters:** `key` — ключ
 * **Returns:** значение, если содержится; null иначе

###### `public void put(K key, V value)`

Добавляет пару "ключ-значение" в асссоциативный массив.

 * **Parameters:**
   * `key` — ключ
   * `value` — значение

###### `public V remove(K key)`

Удаляет пару "ключ-значение" в асссоциативном массиве по ключу.

 * **Parameters:** `key` — ключ
 * **Returns:** удаленное значение, если удалено; null иначе

###### `public void clear()`

Удаляет все элементы из персистентного ассоциативного массива.

###### `public int getWidth()`

Возвращает размер хэш-таблицы.

 * **Returns:** размер хэш-таблицы