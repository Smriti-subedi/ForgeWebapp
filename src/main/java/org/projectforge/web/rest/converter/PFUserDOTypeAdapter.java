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

package org.projectforge.web.rest.converter;

import java.lang.reflect.Type;

import org.projectforge.common.NumberHelper;
import org.projectforge.registry.Registry;
import org.projectforge.user.PFUserDO;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Serialization and deserialization for PFUserDO.
 * @author Kai Reinhard (k.reinhard@micromata.de)
 * 
 */
public class PFUserDOTypeAdapter implements JsonSerializer<PFUserDO>, JsonDeserializer<PFUserDO>
{
  public PFUserDOTypeAdapter()
  {
  }

  @Override
  public synchronized JsonElement serialize(final PFUserDO user, final Type type, final JsonSerializationContext jsonSerializationContext)
  {
    final String id = user != null ? String.valueOf(user.getId()) : "";
    return new JsonPrimitive(id);
  }

  @Override
  public synchronized PFUserDO deserialize(final JsonElement jsonElement, final Type type,
      final JsonDeserializationContext jsonDeserializationContext)
  {
    final Integer userId = NumberHelper.parseInteger(jsonElement.getAsString());
    if (userId == null) {
      return null;
    }
    return Registry.instance().getUserGroupCache().getUser(userId);
  }
}
