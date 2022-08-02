# Changelog

### [1.2.1](https://www.github.com/teletha/typewriter/compare/v1.2.0...v1.2.1) (2022-08-02)


### Bug Fixes

* MariaDB model must use its dialect. ([4a4f4ac](https://www.github.com/teletha/typewriter/commit/4a4f4acda28b23665a05b22bfc03ad01bd2f67f9))
* Speed up H2 related tests. ([1b0fac6](https://www.github.com/teletha/typewriter/commit/1b0fac6cf6cc48cdf8f0e578ce64dc9f6b0c7916))

## [1.2.0](https://www.github.com/teletha/typewriter/compare/v1.1.0...v1.2.0) (2022-08-02)


### Features

* Provide connection pool for RDB. ([feab563](https://www.github.com/teletha/typewriter/commit/feab563e98f3a6e82d0f63f93d7d659a7976de99))
* Provide RDB dialect. ([e868801](https://www.github.com/teletha/typewriter/commit/e868801f3ad7f711e3b9468bae8f989128a95be0))
* Support finding limit size. ([4266d2c](https://www.github.com/teletha/typewriter/commit/4266d2cb69fe0c2e0b88606c63107356bb367922))
* Support MariaDB. ([2c1b48f](https://www.github.com/teletha/typewriter/commit/2c1b48f9db545f2ffb9d07baac8172c6aaa30abe))
* Support multi thread operation. ([f732f3d](https://www.github.com/teletha/typewriter/commit/f732f3dddd64891edd16d4134f0bc6617d070a26))
* Support offset constraint. ([cb2253b](https://www.github.com/teletha/typewriter/commit/cb2253b8702c81585f191de2cd370d4e219c1efa))
* Support Queryable#findAll. ([9f9d026](https://www.github.com/teletha/typewriter/commit/9f9d026fa9429a2acfe11851d3ff9b890fe2da2f))

## [1.1.0](https://www.github.com/teletha/typewriter/compare/v1.0.0...v1.1.0) (2022-07-29)


### Features

* Provide JDBC based backended abstraction. ([7227353](https://www.github.com/teletha/typewriter/commit/7227353554914262c61560de949ce39f72d4e0da))
* Support H2 RDBMS. ([d774545](https://www.github.com/teletha/typewriter/commit/d77454539764210328c631ec18b53911365dba49))

## 1.0.0 (2022-07-28)


### Features

* Add BackendedModel. ([1c3765b](https://www.github.com/teletha/typewriter/commit/1c3765bd33d020969dde41d98267a5c9aebf3cd3))
* Add QueryableModel and special decoder for mongo. ([5f41395](https://www.github.com/teletha/typewriter/commit/5f413958cef1bc270b5e1ae63a955d1c3c15cce1))
* Add QueryableModel#delete. ([10e2823](https://www.github.com/teletha/typewriter/commit/10e2823e7b1b28c70d75b73abc52ee2462074cfa))
* DateConstraint supports #is and #isNot. ([61e8368](https://www.github.com/teletha/typewriter/commit/61e8368799a0e571a7524ce9c1e00f7f1878926f))
* IdentifiableModel hides its ID. ([aca3fb2](https://www.github.com/teletha/typewriter/commit/aca3fb256fdca48e435df850dbad74aea9e63ac4))
* Mongo supports StringConstraint. ([112a4ec](https://www.github.com/teletha/typewriter/commit/112a4ec7289700de50ee8522a6c1a6b3bd71a617))
* Provide the extensible value codec on sqlite. ([21a94e9](https://www.github.com/teletha/typewriter/commit/21a94e9cea34e1d081133bc6186f056ec382806b))
* Provide Updatable, Deletable and Operatable. ([6f34dac](https://www.github.com/teletha/typewriter/commit/6f34dacb7e1db3548d895888d51df96870706e91))
* Queryable is interface now. ([a625c28](https://www.github.com/teletha/typewriter/commit/a625c28e112f22ed290043acd2e3d89125ca4443))
* Restorable can accept the callbak. ([d9c5dd6](https://www.github.com/teletha/typewriter/commit/d9c5dd6916a0b9c08625011c2f3e058184212176))
* SQLite can pool its connection. ([2cff3df](https://www.github.com/teletha/typewriter/commit/2cff3df64649015327bfa1edf632d93c0c04afcd))
* StringConstraint supports #contains. ([31e6aba](https://www.github.com/teletha/typewriter/commit/31e6aba69130ccc4e05b07b2b71d793e9cf80ba2))
* StringConstraint supports regular expression. ([d4aa7ed](https://www.github.com/teletha/typewriter/commit/d4aa7ed01f4e9c02f85aed622ae958098e6b4897))
* StringConstraints support #isEmpty and #isNotEmpty. ([d2c97c3](https://www.github.com/teletha/typewriter/commit/d2c97c3a14f22b91b00ef3ae56f571c358d04345))
* Support java.util.Date constraint. ([c5166a8](https://www.github.com/teletha/typewriter/commit/c5166a8de25c336888c7e17ca611510058deefe9))
* Support LocalDate constriant. ([c88c985](https://www.github.com/teletha/typewriter/commit/c88c9859caaba5912134e2de51f033683bda804d))
* Support LocalDateTime constraint. ([bc3bd82](https://www.github.com/teletha/typewriter/commit/bc3bd82663849dd05094b4d08872432201a6583a))
* Support LocalTime constraint. ([b1718a5](https://www.github.com/teletha/typewriter/commit/b1718a5da6297dffc9d218a52a38a2a6f81c69e9))
* Support Restorable interface. ([d637e2a](https://www.github.com/teletha/typewriter/commit/d637e2a0a34fd18177610230b6261582bb97a5bd))
* Support SQLite. ([11294d4](https://www.github.com/teletha/typewriter/commit/11294d43bfd2e98861273e4872118ec7b2b88256))
* Support transaction. ([2614cd7](https://www.github.com/teletha/typewriter/commit/2614cd7615db26002031b055785cf50ad4f54b45))
* Support ZonedDateTime constraint on mongdb. ([3a2db78](https://www.github.com/teletha/typewriter/commit/3a2db78f815db4bee50122dd5293061587355ba4))
* Support ZonedDateTime constraint on mongodb. ([eb6be7d](https://www.github.com/teletha/typewriter/commit/eb6be7d68d42d080f3c2be6b4992353649bdb4a5))
* Support ZonedDateTime constraint on sqlite. ([5ec6d6a](https://www.github.com/teletha/typewriter/commit/5ec6d6af8d7aee3a979bd2ad4d74e1862c0b168a))
* Use custom id. ([4b5d20a](https://www.github.com/teletha/typewriter/commit/4b5d20afcb4f904d771073601fac7c4493876227))


### Bug Fixes

* Mongo AND filter is broken. ([ad6f48e](https://www.github.com/teletha/typewriter/commit/ad6f48e40287515e2f095fca7e530ccabab904e6))
* Optimize Date on sqlite. ([38fe6f3](https://www.github.com/teletha/typewriter/commit/38fe6f3eb033607cb74f88eb878ad350ad5004be))
* Optimize localdate on sqlite. ([7a394c1](https://www.github.com/teletha/typewriter/commit/7a394c13e31dca67b7584d44ca8e37e6da863c7d))
* Rename constrains methods. ([883a57d](https://www.github.com/teletha/typewriter/commit/883a57d341ad7dda70c524ddcfca8d6b895849b6))
