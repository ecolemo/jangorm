Java ORM that provides django-like syntax.

Design principles
 - Django-like model definition rather than Rails-like migration
 - No magic like ActiveJDBC or Rails
 - Provide Model superclass, not like ORMLite
 - Using with Model class & objects, not with PersistantManager, DAO, other global managers.
 - Support join, outer join.
 - Ready to support non-jdbc DBMS like Android or NoSQL

Versus ActiveJDBC
 - Generate DB tables.
 - No magic
 - More verbose syntax than ActiveJDBC.

Versus ORMLite
 - No DAO, use model directly.
 - Support join, outer join.
 - contains data in map

Versus Hibernate
 - Simpler configuration
 - No Persistant Manger, use model directly.

