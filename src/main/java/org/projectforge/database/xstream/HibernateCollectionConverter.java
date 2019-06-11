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

package org.projectforge.database.xstream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.hibernate.collection.internal.PersistentSet;
import org.hibernate.collection.internal.PersistentSortedSet;
import org.hibernate.collection.spi.PersistentCollection;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * XStream converter that strips HB collections specific information and retrieves the underlying collection which is then parsed by the
 * delegated converter. This converter only takes care of the values inside the collections while the mapper takes care of the collections
 * naming.
 * 
 * @author Costin Leau
 * 
 */
public class HibernateCollectionConverter implements Converter
{
  private final Converter listSetConverter;

  private final Converter mapConverter;

  private final Converter treeMapConverter;

  private final Converter treeSetConverter;

  private final Converter defaultConverter;

  public HibernateCollectionConverter(final ConverterLookup converterLookup)
  {
    listSetConverter = converterLookup.lookupConverterForType(ArrayList.class);
    mapConverter = converterLookup.lookupConverterForType(HashMap.class);
    treeMapConverter = converterLookup.lookupConverterForType(TreeMap.class);
    treeSetConverter = converterLookup.lookupConverterForType(TreeSet.class);
    defaultConverter = converterLookup.lookupConverterForType(Object.class);
  }

  /**
   * @see com.thoughtworks.xstream.converters.Converter#canConvert(java.lang.Class)
   */
  public boolean canConvert(final Class type)
  {
    return PersistentCollection.class.isAssignableFrom(type);
  }

  /**
   * @see com.thoughtworks.xstream.converters.Converter#marshal(java.lang.Object, com.thoughtworks.xstream.io.HierarchicalStreamWriter,
   *      com.thoughtworks.xstream.converters.MarshallingContext)
   */
  public void marshal(final Object source, final HierarchicalStreamWriter writer, final MarshallingContext context)
  {
    Object collection = source;

    if (source instanceof PersistentCollection) {
      final PersistentCollection col = (PersistentCollection) source;
      col.forceInitialization();
      // ToDo ES: collection = col.getCollectionSnapshot().getSnapshot();
      collection = col.getStoredSnapshot();
    }

    // the set is returned as a map by Hibernate (unclear why exactly)
    if (source instanceof PersistentSortedSet) {
      collection = new TreeSet(((HashMap) collection).values());
    } else if (source instanceof PersistentSet) {
      // collection = new HashSet(((HashMap)collection).entrySet());
      collection = new HashSet(((HashMap) collection).values());
    }

    // delegate the collection to the approapriate converter
    if (listSetConverter.canConvert(collection.getClass())) {
      listSetConverter.marshal(collection, writer, context);
      return;
    }
    if (mapConverter.canConvert(collection.getClass())) {
      mapConverter.marshal(collection, writer, context);
      return;
    }
    if (treeMapConverter.canConvert(collection.getClass())) {
      treeMapConverter.marshal(collection, writer, context);
      return;
    }
    if (treeSetConverter.canConvert(collection.getClass())) {
      treeSetConverter.marshal(collection, writer, context);
      return;
    }

    defaultConverter.marshal(collection, writer, context);
  }

  /**
   * @see com.thoughtworks.xstream.converters.Converter#unmarshal(com.thoughtworks.xstream.io.HierarchicalStreamReader,
   *      com.thoughtworks.xstream.converters.UnmarshallingContext)
   */
  public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext context)
  {
    return null;
  }
}
