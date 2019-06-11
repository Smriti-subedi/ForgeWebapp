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

package org.projectforge.meb;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.DateBridge;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.annotations.Resolution;
import org.hibernate.search.annotations.Store;
import org.projectforge.core.AbstractBaseDO;
import org.projectforge.user.PFUserDO;

/**
 * @author Kai Reinhard (k.reinhard@micromata.de)
 * 
 */
@Entity
@Indexed
@Table(name = "T_MEB_ENTRY")
public class MebEntryDO extends AbstractBaseDO<Integer>
{
  private static final long serialVersionUID = 4424813938259685100L;

  @Field(analyze = Analyze.NO, store = Store.NO)
  private Integer id;

  @IndexedEmbedded(depth = 1)
  private PFUserDO owner;

  @Field(store = Store.NO)
  private String sender;

  @Field(store = Store.NO)
  private String message;

  @Field(analyze = Analyze.NO)
  @DateBridge(resolution = Resolution.DAY)
  private Date date;

  private MebEntryStatus status;

  @Id
  @GeneratedValue
  @Column(name = "pk")
  public Integer getId()
  {
    return id;
  }

  public void setId(final Integer id)
  {
    this.id = id;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "owner_fk")
  public PFUserDO getOwner()
  {
    return owner;
  }

  @Transient
  public Integer getOwnerId()
  {
    if (owner == null) {
      return null;
    } else {
      return owner.getId();
    }
  }

  public MebEntryDO setOwner(final PFUserDO owner)
  {
    this.owner = owner;
    return this;
  }

  @Column(length = 255, nullable = false)
  public String getSender()
  {
    return sender;
  }

  public MebEntryDO setSender(final String sender)
  {
    this.sender = sender;
    return this;
  }

  @Column(length = 4000)
  public String getMessage()
  {
    return message;
  }

  public void setMessage(final String message)
  {
    this.message = message;
  }

  @Enumerated(EnumType.STRING)
  @Column(length = 20, nullable = false)
  public MebEntryStatus getStatus()
  {
    return status;
  }

  public MebEntryDO setStatus(final MebEntryStatus status)
  {
    this.status = status;
    return this;
  }

  @Column(nullable = false)
  public Date getDate()
  {
    return date;
  }

  public MebEntryDO setDate(final Date date)
  {
    this.date = date;
    return this;
  }
}
