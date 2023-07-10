# Changelog

## [1.7.0](https://github.com/teletha/typewriter/compare/v1.6.0...v1.7.0) (2023-07-10)


### Features

* Add 'typewriter.connection.autoCommit readOnly isolation' config. ([856b5c8](https://github.com/teletha/typewriter/commit/856b5c8037648cbe8d3224ce4bd80a97c76ca245))
* Add 'typewriter.connection.singleton' config. ([9045bbe](https://github.com/teletha/typewriter/commit/9045bbefa1d2f8232114aa06408f9333a738b9bc))
* Add BackendedModel#saveLazily with default delay. ([24ca70f](https://github.com/teletha/typewriter/commit/24ca70fdb9f05740c37d8833ad3d4e1d5e33ae5b))
* Add BackendedModel#saveLazily. ([4ed7242](https://github.com/teletha/typewriter/commit/4ed72426c5dc34ddfe43b22546495bc845499665))
* RDBCodec support null column. ([986d071](https://github.com/teletha/typewriter/commit/986d07104335cd5b6c1cea904ddefeafc7b2af15))
* SQLite is more configurable. ([5440652](https://github.com/teletha/typewriter/commit/544065230e01af058b4e5e1a8e207f1f6be0e77a))
* Support primitive wrapper types. ([9cd071a](https://github.com/teletha/typewriter/commit/9cd071a49f6d281302a86901cac76c63abb5edeb))

## [1.6.0](https://github.com/teletha/typewriter/compare/v1.5.0...v1.6.0) (2023-07-03)


### Features

* Add general codec for enum. ([45915d6](https://github.com/teletha/typewriter/commit/45915d617ec3fa1850fe267d83a09ab326b505e5))
* Add various List constraints. ([e029268](https://github.com/teletha/typewriter/commit/e029268480e163bb7dec584fdd3535d65d68d54d))
* BackendedModel supports delayed saving. ([5f805e1](https://github.com/teletha/typewriter/commit/5f805e11aec332ec2bec8e006ed2bd8597927762))
* Dialect can create the specialized ListConstraint. ([0aeabf2](https://github.com/teletha/typewriter/commit/0aeabf2642a08863016b11b4d114d648a8fc5b08))
* Enable error log. ([7870b6b](https://github.com/teletha/typewriter/commit/7870b6bf7357089dfd1aca126da2a59739898d16))
* H2 supports ListConstraint. ([a9e600a](https://github.com/teletha/typewriter/commit/a9e600ad11bbdfe83b71caa1fc1dcc53c31a283f))
* LocalDate codec supports null. ([eb6ee9c](https://github.com/teletha/typewriter/commit/eb6ee9c4f5efc8d75325805e19ac869f18731f5a))
* RDB can detect dialect from environment. ([c13f65f](https://github.com/teletha/typewriter/commit/c13f65f9cd1653ac614252dbe5b17dd6a7f32256))
* Support additional property migration. ([b86e0f6](https://github.com/teletha/typewriter/commit/b86e0f6bfb084cc1c6a6ab6687b5a504b42f7a02))
* Support ListConstraint on sqlite. ([c5834dc](https://github.com/teletha/typewriter/commit/c5834dcf2c6104f411b6f55dcf043d797230ee25))
* Support multiple update. ([0d090d1](https://github.com/teletha/typewriter/commit/0d090d1504099be3142df4d4c8c656a2c1cb7adc))
* Support name-based property update on RDB backend. ([37e1f77](https://github.com/teletha/typewriter/commit/37e1f77ad4887701682317fd09954a8712c799ae))
* Try to support 1:n relationship. ([790cf98](https://github.com/teletha/typewriter/commit/790cf9803c1671c0faf0e36276d51cb9d6691685))


### Bug Fixes

* Add slf4j-nop explicitly. ([9dc4af8](https://github.com/teletha/typewriter/commit/9dc4af83619e85928c01efa49b3175ba324b2d39))
* Can't search the configured db location. ([f63b281](https://github.com/teletha/typewriter/commit/f63b28197b61b8f83cd9a404c39e82c8f0968257))
* Correct DB setting on H2. ([dcc9b6c](https://github.com/teletha/typewriter/commit/dcc9b6c057d8b0e62adaa49cb63b9c8aeb4d69c0))
* correct type inference ([b2590dd](https://github.com/teletha/typewriter/commit/b2590ddfe013990a38734949c8e45ea0b6262a7b))
* H2DB uses memory db on test. ([e62295c](https://github.com/teletha/typewriter/commit/e62295ca43aa7947b739d71eebbf9708dec4b3cd))
* Optimize query builder. ([eededb8](https://github.com/teletha/typewriter/commit/eededb8edc9f3ac9892442306ae3b0cae079d4a3))
* Update MariaDB. ([166d53b](https://github.com/teletha/typewriter/commit/166d53bc040fa4341f360f244f20f854ed4675d7))
* We should detect dialect from model type. ([a44830c](https://github.com/teletha/typewriter/commit/a44830c32d6f9e8d941f7f18af79ab2fa917c32e))

## [1.5.0](https://github.com/teletha/typewriter/compare/v1.4.0...v1.5.0) (2023-04-25)


### Features

* Support error handling in BackendedModel. ([adf0a45](https://github.com/teletha/typewriter/commit/adf0a4513b8b102c91be1ce8cab64fd80e3f1dfa))


### Bug Fixes

* downgrade mariadb4j to avoid bug ([c800110](https://github.com/teletha/typewriter/commit/c800110ee16b0abd8152b87af0326f546529e0b0))
* MongoDB can check the existence of property. ([f5ab950](https://github.com/teletha/typewriter/commit/f5ab95041d83ec5b9f1771b9243fb92a1bc63384))

## [1.4.0](https://www.github.com/teletha/typewriter/compare/v1.3.0...v1.4.0) (2022-08-30)


### Features

* Add BackendModel#restoring instead of #restore(Consumer<M>). ([75ea065](https://www.github.com/teletha/typewriter/commit/75ea06565b7c4873b118c0484700627bb1307c07))
* Provide Queryable#page as pagination helper. ([fc3929f](https://www.github.com/teletha/typewriter/commit/fc3929f6b86f8d5525671baa59b0314a74ee149b))
* Support accumulable operators. (MIN MAX SVG SUM DISTINCT) ([55720f1](https://www.github.com/teletha/typewriter/commit/55720f1bdfda79ce7d35ae9e1e6011fba31033be))
* Support distinct by single property. ([77a1265](https://www.github.com/teletha/typewriter/commit/77a1265542326780873651a872c73174c6aff0aa))
* Support min accumulator. ([11181d3](https://www.github.com/teletha/typewriter/commit/11181d33c1551945660dc5d66f50afa22b2c70b0))
* Support OffsetDateTime. ([d034253](https://www.github.com/teletha/typewriter/commit/d03425302f55d309fa37265393147b46b96f7821))


### Bug Fixes

* SQL functions use lower case name. ([de11f2c](https://www.github.com/teletha/typewriter/commit/de11f2c0319cd923cc2f5b30e945368f6a4d6af5))
* Update sinobu. ([60b73ed](https://www.github.com/teletha/typewriter/commit/60b73ede2078281de52c223eb73afd155c766546))

## [1.3.0](https://www.github.com/teletha/typewriter/compare/v1.2.0...v1.3.0) (2022-08-04)


### Features

* Support sort by text. ([838227b](https://www.github.com/teletha/typewriter/commit/838227b5a9f08d237962d679a01f5069b2f9e625))
* Support sorting by Data and LocalData. ([57c3f15](https://www.github.com/teletha/typewriter/commit/57c3f15c304d40134ac56d940bf003ce3d427746))
* Support sorting by multiple properties. ([dbd9636](https://www.github.com/teletha/typewriter/commit/dbd9636eb3a491f43a8d97111e950d814f5dea0a))
* Support sorting by various date related API. ([86d70b1](https://www.github.com/teletha/typewriter/commit/86d70b1ebca6f819a4f6f99269bbb4c1fa52dd0c))
* Support sorting. ([662c2d0](https://www.github.com/teletha/typewriter/commit/662c2d00b78d5e1dbe7f309b3f5ccf3c452e46f5))
* Support various character set. ([b3eb7a4](https://www.github.com/teletha/typewriter/commit/b3eb7a4ee2479aa3af38213a7bb4bf290468f4ac))


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
