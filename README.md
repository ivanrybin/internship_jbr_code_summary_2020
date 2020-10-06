# Тестовое задание на проект JBR - code summarization

### Детали реализации 
Запуск: через IDEA (run) 

#### Механизм работы
  - на ввод подается ссылка на репозиторий
  - клонирует указанный репозиторий по адресу `git_repos/имя_репозитория`, 
    где `имя_репозитория` = суффикс после последнего `/` в ссылке на репозиторий
  - получает список хешей и дат коммитов master ветки
  - стартует с первого (самого старого) коммита репозитория 
  - для всех последовательных пар коммитов рассматривает файлы из `git diff --name-only comm1 comm2`,
    т.е. те файлы, в которых были произведены изменения между двумя последовательными коммитами
  - если множество таких файлов не пусто - делает `git checkout comm2`
  - с помощью грамматик для ANTLR парсит файлы поддерживаемых языков - достает имена методов вида с сохранением вложенности: `[class.][subclass.][method.][submethod.]NAME`
  - сохраняет дату и хеш коммита, относящегося к первому и последнему вхождению метода в исследуемые файлы 
  - делает JSON дамп в папку `results` со всей информацией
  - выводит топ 10 долгоживущих методов
 
#### Коротко
 
Пробег от самого старого коммита к самому новому с `checkout` между последовательными коммитами, если между ними есть изменения поддерживаемых для поиска файлов.

Каждый раз, когда уже рассмотренный метод в конкретном файле, классе, методе встречается на `N`-м коммите, происходит сохранение последней даты и коммита его появления.

Каждый метод идентифицируется через файл проекта, имена структур, его покрывающих, и собственное имя.
  
#### Поиск имен методов
  - реализован через парсеры и лексеры [ANTLR](https://github.com/antlr/antlr4), сгенерированные в Java код из грамматик [grammars-v4](https://github.com/antlr/grammars-v4)
  - в проекте используются грамматики для Java, Kotlin, Python, CSharp

#### Отбор файлов для поиска
  - пропускает скрытые файлы
  - пропускает любые файлы в скрытых папках

Пример работы на [моем репозитории по курсу Software Design](https://github.com/ivanrybin/itmo-hws-architecture):

      Input git repository URL:
      https://github.com/ivanrybin/itmo-hws-architecture
      > cloning repository in 'git_repos' dir successful
      > commits history collected (82 commits)
      > methods search running ...
      > 25% done
      > 50% done
      > 75% done
      > 90% done
      > methods search done
      > data was dumped in 'results' dir
      TOP METHODS:
      2020-06-23 -> 2020-07-11	days: 18     name: MessageLog.add_message        hash: 8dc79e -> 3f73df		path: /roguelike/project/logic/logger.py
      2020-06-23 -> 2020-07-11	days: 18     name: MessageLog.__init__           hash: 8dc79e -> 3f73df		path: /roguelike/project/logic/logger.py
      2020-06-23 -> 2020-07-11	days: 18     name: Message.__init__              hash: 8dc79e -> 3f73df		path: /roguelike/project/logic/logger.py
      2020-06-23 -> 2020-07-11	days: 18     name: Inventory.del_item            hash: 8dc79e -> 3f73df		path: /roguelike/project/logic/inventory.py
      2020-06-23 -> 2020-07-11	days: 18     name: Inventory.add_item            hash: 8dc79e -> 3f73df		path: /roguelike/project/logic/inventory.py
      2020-06-23 -> 2020-07-11	days: 18     name: Inventory.__init__            hash: 8dc79e -> 3f73df		path: /roguelike/project/logic/inventory.py
      2020-06-23 -> 2020-07-11	days: 18     name: Item.__init__                 hash: 8dc79e -> 3f73df		path: /roguelike/project/logic/inventory.py
      2020-06-23 -> 2020-07-11	days: 18     name: inventory_menu                hash: 8dc79e -> 3f73df		path: /roguelike/project/engine/menu.py
      2020-06-23 -> 2020-07-11	days: 18     name: menu                          hash: 8dc79e -> 3f73df		path: /roguelike/project/engine/menu.py
      2020-06-23 -> 2020-07-11	days: 18     name: EntityStats.increase_hp       hash: 8dc79e -> 3f73df		path: /roguelike/project/logic/entity_stats.py
          
 Можно запустить в корне большого проекта -- скорость поиска напрямую связана с парсингом через ANTLR.
   
 ### Что можно улучшить
  - добавить поддержку файлов других языков через иные лексеры, парсеры и грамматики под них
  - добавить поддержку рассмотрения конкретных веток
