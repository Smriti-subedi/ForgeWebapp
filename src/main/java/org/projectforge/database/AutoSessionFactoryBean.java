/////////////////////////////////////////////////////////////////////////////
//
// Project ProjectForge Community Edition
//         www.projectforge.org
//
// Copyright (C) 2001-2014 Kai Reinhard (k.reinhard@micromata.de)
//
// ProjectForge is dual-licensed.
//
// This community edition is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License as published
// by the Free Software Foundation; version 3 of the License.
//
// This community edition is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
// Public License for more details.
//
// You should have received a copy of the GNU General Public License along
// with this program; if not, see http://www.gnu.org/licenses/.
//
/////////////////////////////////////////////////////////////////////////////

package org.projectforge.database;

import java.lang.annotation.ElementType;

import org.hibernate.HibernateException;
import org.hibernate.cfg.Configuration;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Resolution;
import org.hibernate.search.annotations.Store;
import org.hibernate.search.cfg.SearchMapping;
import org.projectforge.core.ConfigXml;
import org.projectforge.plugins.core.AbstractPlugin;
import org.projectforge.plugins.core.PluginsRegistry;
import org.springframework.orm.hibernate3.annotation.AnnotationSessionFactoryBean;

import de.micromata.hibernate.history.HistoryEntry;
import de.micromata.hibernate.history.delta.PropertyDelta;

/**
 * @author Wolfgang Jung (w.jung@micromata.de)
 * @author Kai Reinhard (k.reinhard@micromata.de)
 */
public class AutoSessionFactoryBean extends AnnotationSessionFactoryBean
{
  /** The logger */
  private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(AutoSessionFactoryBean.class);

  private boolean schemaUpdate;

  /**
   * @see org.springframework.orm.hibernate4.LocalSessionFactoryBean#postProcessConfiguration(org.hibernate.cfg.Configuration)
   */
  @Override
  protected void postProcessConfiguration(final Configuration config) throws HibernateException
  {
    for (final Class< ? > entityClass : HibernateEntities.CORE_ENTITIES) {
      log.debug("Adding class " + entityClass.getName());
      config.addAnnotatedClass(entityClass);
    }
    for (final Class< ? > entityClass : HibernateEntities.HISTORY_ENTITIES) {
      log.debug("Adding class " + entityClass.getName());
      config.addAnnotatedClass(entityClass);
    }
    final PluginsRegistry pluginsRegistry = PluginsRegistry.instance();
    pluginsRegistry.loadPlugins();
    for (final AbstractPlugin plugin : pluginsRegistry.getPlugins()) {
      final Class< ? >[] persistentEntities = plugin.getPersistentEntities();
      if (persistentEntities != null) {
        for (final Class< ? > entity : persistentEntities) {
          log.debug("Adding class " + entity.getName());
          config.addAnnotatedClass(entity);
          HibernateEntities.instance().addEntity(entity);
        }
      }
    }

    // Add the hibernate history entities programmatically:
    final SearchMapping mapping = new SearchMapping();
    mapping.entity(HistoryEntry.class).indexed() //
    .property("id", ElementType.METHOD).documentId().name("id")//
    .property("userName", ElementType.METHOD).field().analyze(Analyze.NO).store(Store.NO) //
    // Must be tokenized for using lower case (MultiFieldQueryParser uses lower case strings):
    .property("className", ElementType.METHOD).field().store(Store.NO) //
    .property("timestamp", ElementType.METHOD).field().store(Store.NO).dateBridge(Resolution.MINUTE) //
    // Needed in BaseDao for FullTextQuery.setProjection("entityId"):
    .property("entityId", ElementType.METHOD).field().store(Store.YES) //
    .property("delta", ElementType.METHOD).indexEmbedded() //
    // PropertyDelta:
    .entity(PropertyDelta.class) //
    .property("id", ElementType.METHOD).documentId().name("id")//
    .property("oldValue", ElementType.METHOD).field().store(Store.NO) //
    .property("newValue", ElementType.METHOD).field().store(Store.NO); //
    config.getProperties().put("hibernate.search.model_mapping", mapping);
    super.postProcessConfiguration(config);
  }

  /**
   * Nach dem Update des Schema die Datenbank mit den in der XML-Datei angegebenen Objekten befüllt.
   * @see org.springframework.orm.hibernate4.LocalSessionFactoryBean#updateDatabaseSchema()
   */
  @Override
  public void afterPropertiesSet()
  {
    super.setSchemaUpdate(false);
    try {
      super.afterPropertiesSet();
    } catch (final Exception ex) {
      throw new RuntimeException(ex);
    }
    if (schemaUpdate == true) {
      super.setSchemaUpdate(schemaUpdate);
      updateDatabaseSchema();
    }
  }

  @Override
  public void setSchemaUpdate(final boolean schemaUpdate)
  {
    super.setSchemaUpdate(schemaUpdate);
    this.schemaUpdate = schemaUpdate;
  }

  /**
   * Needed for ensuring that configuration is initialized.
   * @param configXml
   */
  public void setConfigXml(final ConfigXml configXml)
  {
    // Do nothing. Ensure only that configuration is initialized.
  }
}
