const root = {
	"docs": [],
	"modules": [],
	"packages": [
		"typewriter.sqlite",
		"typewriter.rdb",
		"typewriter.maria",
		"typewriter.h2",
		"typewriter.mongo",
		"typewriter.duck",
		"typewriter.api",
		"typewriter.api.model",
		"typewriter.query",
		"typewriter.postgres"
	],
	"types": [
		{
			"name": "SQLite",
			"packageName": "typewriter.sqlite",
			"type": "Class"
		},
		{
			"name": "SQLiteModel",
			"packageName": "typewriter.sqlite",
			"type": "AbstractClass"
		},
		{
			"name": "RDBCodec",
			"packageName": "typewriter.rdb",
			"type": "AbstractClass"
		},
		{
			"name": "RDB",
			"packageName": "typewriter.rdb",
			"type": "Class"
		},
		{
			"name": "SQL",
			"packageName": "typewriter.rdb",
			"type": "Class"
		},
		{
			"name": "Dialect",
			"packageName": "typewriter.rdb",
			"type": "AbstractClass"
		},
		{
			"name": "MariaModel",
			"packageName": "typewriter.maria",
			"type": "AbstractClass"
		},
		{
			"name": "MariaDB",
			"packageName": "typewriter.maria",
			"type": "Class"
		},
		{
			"name": "H2",
			"packageName": "typewriter.h2",
			"type": "Class"
		},
		{
			"name": "Functions",
			"packageName": "typewriter.h2",
			"type": "Class"
		},
		{
			"name": "H2Model",
			"packageName": "typewriter.h2",
			"type": "AbstractClass"
		},
		{
			"name": "Mongo",
			"packageName": "typewriter.mongo",
			"type": "Class"
		},
		{
			"name": "MongoModel",
			"packageName": "typewriter.mongo",
			"type": "AbstractClass"
		},
		{
			"name": "MongoQuery",
			"packageName": "typewriter.mongo",
			"type": "Class"
		},
		{
			"name": "DuckModel",
			"packageName": "typewriter.duck",
			"type": "AbstractClass"
		},
		{
			"name": "DuckDB",
			"packageName": "typewriter.duck",
			"type": "Class"
		},
		{
			"name": "Specifier",
			"packageName": "typewriter.api",
			"type": "Functional"
		},
		{
			"name": "Specifier.ConditionSpecifier",
			"packageName": "typewriter.api",
			"type": "Interface"
		},
		{
			"name": "Specifier.ListSpecifier",
			"packageName": "typewriter.api",
			"type": "Functional"
		},
		{
			"name": "Specifier.ZonedDateTimeSpecifier",
			"packageName": "typewriter.api",
			"type": "Functional"
		},
		{
			"name": "Specifier.OffsetDateTimeSpecifier",
			"packageName": "typewriter.api",
			"type": "Functional"
		},
		{
			"name": "Specifier.LocalDateTimeSpecifier",
			"packageName": "typewriter.api",
			"type": "Functional"
		},
		{
			"name": "Specifier.LocalTimeSpecifier",
			"packageName": "typewriter.api",
			"type": "Functional"
		},
		{
			"name": "Specifier.LocalDateSpecifier",
			"packageName": "typewriter.api",
			"type": "Functional"
		},
		{
			"name": "Specifier.DateSpecifier",
			"packageName": "typewriter.api",
			"type": "Functional"
		},
		{
			"name": "Specifier.StringSpecifier",
			"packageName": "typewriter.api",
			"type": "Functional"
		},
		{
			"name": "Specifier.BooleanSpecifier",
			"packageName": "typewriter.api",
			"type": "Functional"
		},
		{
			"name": "Specifier.CharSpecifier",
			"packageName": "typewriter.api",
			"type": "Functional"
		},
		{
			"name": "Specifier.NumericSpecifier",
			"packageName": "typewriter.api",
			"type": "Functional"
		},
		{
			"name": "Accumulable",
			"packageName": "typewriter.api",
			"type": "Interface"
		},
		{
			"name": "LazyUpdatable",
			"packageName": "typewriter.api",
			"type": "AbstractClass"
		},
		{
			"name": "BackendedModel",
			"packageName": "typewriter.api.model",
			"type": "AbstractClass"
		},
		{
			"name": "IdentifiableModel",
			"packageName": "typewriter.api.model",
			"type": "AbstractClass"
		},
		{
			"name": "Identifiable",
			"packageName": "typewriter.api",
			"type": "Functional"
		},
		{
			"name": "Restorable",
			"packageName": "typewriter.api",
			"type": "Functional"
		},
		{
			"name": "Constraint",
			"packageName": "typewriter.api",
			"type": "Interface"
		},
		{
			"name": "Constraint.ListConstraint",
			"packageName": "typewriter.api",
			"type": "Interface"
		},
		{
			"name": "Constraint.ZonedDateTimeConstraint",
			"packageName": "typewriter.api",
			"type": "Interface"
		},
		{
			"name": "Constraint.OffsetDateTimeConstraint",
			"packageName": "typewriter.api",
			"type": "Interface"
		},
		{
			"name": "Constraint.LocalDateTimeConstraint",
			"packageName": "typewriter.api",
			"type": "Interface"
		},
		{
			"name": "Constraint.LocalTimeConstraint",
			"packageName": "typewriter.api",
			"type": "Interface"
		},
		{
			"name": "Constraint.LocalDateConstraint",
			"packageName": "typewriter.api",
			"type": "Interface"
		},
		{
			"name": "Constraint.DateConstraint",
			"packageName": "typewriter.api",
			"type": "Interface"
		},
		{
			"name": "Constraint.TemporalConstraint",
			"packageName": "typewriter.api",
			"type": "Interface"
		},
		{
			"name": "Constraint.StringConstraint",
			"packageName": "typewriter.api",
			"type": "Interface"
		},
		{
			"name": "Constraint.NumericConstraint",
			"packageName": "typewriter.api",
			"type": "Interface"
		},
		{
			"name": "Constraint.TypeConstraint",
			"packageName": "typewriter.api",
			"type": "Interface"
		},
		{
			"name": "Updatable",
			"packageName": "typewriter.api",
			"type": "Interface"
		},
		{
			"name": "Queryable",
			"packageName": "typewriter.api",
			"type": "Interface"
		},
		{
			"name": "Migration",
			"packageName": "typewriter.api",
			"type": "AbstractClass"
		},
		{
			"name": "Metadatable",
			"packageName": "typewriter.api",
			"type": "Interface"
		},
		{
			"name": "Transactional",
			"packageName": "typewriter.api",
			"type": "Functional"
		},
		{
			"name": "Deletable",
			"packageName": "typewriter.api",
			"type": "Functional"
		},
		{
			"name": "QueryExecutor",
			"packageName": "typewriter.api",
			"type": "AbstractClass"
		},
		{
			"name": "SQLCoder",
			"packageName": "typewriter.query",
			"type": "Class"
		},
		{
			"name": "SQLCodingOption",
			"packageName": "typewriter.query",
			"type": "Class"
		},
		{
			"name": "AVGOption",
			"packageName": "typewriter.query",
			"type": "Class"
		},
		{
			"name": "PostgreSQL",
			"packageName": "typewriter.postgres",
			"type": "Class"
		},
		{
			"name": "PostgresModel",
			"packageName": "typewriter.postgres",
			"type": "AbstractClass"
		}
	]
}