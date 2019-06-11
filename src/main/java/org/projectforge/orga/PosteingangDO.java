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

package org.projectforge.orga;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.DateBridge;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Resolution;
import org.hibernate.search.annotations.Store;
import org.projectforge.core.DefaultBaseDO;

/**
 * Posteingangsbuch
 * @author Kai Reinhard (k.reinhard@micromata.de)
 * 
 */
@Entity
@Indexed
@Table(name = "T_ORGA_POSTEINGANG")
public class PosteingangDO extends DefaultBaseDO
{
  private static final long serialVersionUID = -4713747110526000256L;

  @Field(analyze = Analyze.NO)
  @DateBridge(resolution = Resolution.DAY)
  private Date datum;

  @Field(store = Store.NO)
  private String absender;

  @Field(store = Store.NO)
  private String person;

  @Field(store = Store.NO)
  private String inhalt;

  @Field(store = Store.NO)
  private String bemerkung;

  @Field(analyze = Analyze.NO, store = Store.NO)
  private PostType type;

  @Column(nullable = false)
  public Date getDatum()
  {
    return datum;
  }

  public void setDatum(final Date datum)
  {
    this.datum = datum;
  }

  @Column(name = "person", length = 1000)
  public String getPerson()
  {
    return person;
  }

  public void setPerson(final String person)
  {
    this.person = person;
  }

  @Column(name = "absender", length = 1000, nullable = false)
  public String getAbsender()
  {
    return absender;
  }

  public void setAbsender(final String absender)
  {
    this.absender = absender;
  }

  @Column(length = 1000)
  public String getInhalt()
  {
    return inhalt;
  }

  public void setInhalt(final String inhalt)
  {
    this.inhalt = inhalt;
  }

  @Column(length = 4000)
  public String getBemerkung()
  {
    return bemerkung;
  }

  public void setBemerkung(final String bemerkung)
  {
    this.bemerkung = bemerkung;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "post_type", length = 20, nullable = false)
  public PostType getType()
  {
    return type;
  }

  public void setType(final PostType type)
  {
    this.type = type;
  }
}
