package models

import play.api.ApplicationLoader.Context
import play.api.BuiltInComponentsFromContext
import play.api.db.{DBComponents, HikariCPComponents}
import play.api.db.evolutions.EvolutionsComponents
import play.filters.HttpFiltersComponents

abstract class AppComponents(cntx: Context)
    extends BuiltInComponentsFromContext(cntx)
    with DBComponents
    with EvolutionsComponents
    with HikariCPComponents
    with HttpFiltersComponents {
  // this will actually run the database migrations on startup
  applicationEvolutions
}
