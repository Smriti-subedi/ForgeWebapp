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

package org.projectforge.fibu;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
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
import org.projectforge.core.Constants;
import org.projectforge.core.DefaultBaseDO;
import org.projectforge.core.PropertyInfo;
import org.projectforge.fibu.kost.Kost1DO;
import org.projectforge.user.PFUserDO;

/**
 * Repräsentiert einen Mitarbeiter. Ein Mitarbeiter ist einem ProjectForge-Benutzer zugeordnet und enthält buchhalterische Angaben.
 * @author Kai Reinhard (k.reinhard@micromata.de)
 */
@Entity
@Indexed
@Table(name = "t_fibu_employee")
public class EmployeeDO extends DefaultBaseDO
{
  private static final long serialVersionUID = -1208597049289694757L;

  @PropertyInfo(i18nKey = "fibu.employee.user")
  @IndexedEmbedded(depth = 1)
  private PFUserDO user;

  @PropertyInfo(i18nKey = "fibu.kost1")
  @IndexedEmbedded(depth = 1)
  private Kost1DO kost1;

  @PropertyInfo(i18nKey = "status")
  @Field(store = Store.NO)
  private EmployeeStatus status;

  @PropertyInfo(i18nKey = "address.positionText")
  @Field(store = Store.NO)
  private String position;

  @PropertyInfo(i18nKey = "fibu.employee.eintrittsdatum")
  @Field(analyze = Analyze.NO)
  @DateBridge(resolution = Resolution.DAY)
  private Date eintrittsDatum;

  @PropertyInfo(i18nKey = "fibu.employee.austrittsdatum")
  @Field(analyze = Analyze.NO)
  @DateBridge(resolution = Resolution.DAY)
  private Date austrittsDatum;

  @PropertyInfo(i18nKey = "address.division")
  @Field(store = Store.NO)
  private String abteilung;

  @PropertyInfo(i18nKey = "fibu.employee.urlaubstage")
  @Field(analyze = Analyze.NO, store = Store.NO)
  private Integer urlaubstage;

  @PropertyInfo(i18nKey = "fibu.employee.wochenstunden")
  @Field(analyze = Analyze.NO, store = Store.NO)
  private BigDecimal weeklyWorkingHours;

  @PropertyInfo(i18nKey = "comment")
  @Field(store = Store.NO)
  private String comment;

  @Enumerated(EnumType.STRING)
  @Column(name = "employee_status", length = 30)
  public EmployeeStatus getStatus()
  {
    return status;
  }

  public void setStatus(final EmployeeStatus status)
  {
    this.status = status;
  }

  /**
   * Dem Benutzer zugeordneter Kostenträger Kost1 für den Monatsreport.
   */
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "kost1_id", nullable = true)
  public Kost1DO getKost1()
  {
    return kost1;
  }

  public void setKost1(final Kost1DO kost1)
  {
    this.kost1 = kost1;
  }

  @Transient
  public Integer getKost1Id()
  {
    if (this.kost1 == null)
      return null;
    return kost1.getId();
  }

  /**
   * The ProjectForge user assigned to this employee.
   * @return the user
   */
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "user_id", nullable = false, unique = true)
  public PFUserDO getUser()
  {
    return user;
  }

  /**
   * @param user the user to set
   */
  public void setUser(final PFUserDO user)
  {
    this.user = user;
  }

  @Transient
  public Integer getUserId()
  {
    if (this.user == null)
      return null;
    return user.getId();
  }

  @Column
  public Integer getUrlaubstage()
  {
    return urlaubstage;
  }

  public void setUrlaubstage(final Integer urlaubstage)
  {
    this.urlaubstage = urlaubstage;
  }

  @Column(name="weekly_working_hours", scale = 5, precision = 10)
  public BigDecimal getWeeklyWorkingHours()
  {
    return weeklyWorkingHours;
  }

  public void setWeeklyWorkingHours(final BigDecimal weeklyWorkingHours)
  {
    this.weeklyWorkingHours = weeklyWorkingHours;
  }

  @Column(name = "eintritt")
  public Date getEintrittsDatum()
  {
    return eintrittsDatum;
  }

  public void setEintrittsDatum(final Date eintrittsDatum)
  {
    this.eintrittsDatum = eintrittsDatum;
  }

  @Column(name = "austritt")
  public Date getAustrittsDatum()
  {
    return austrittsDatum;
  }

  public void setAustrittsDatum(final Date austrittsDatum)
  {
    this.austrittsDatum = austrittsDatum;
  }

  @Column(name = "position_text", length = 244)
  public String getPosition()
  {
    return position;
  }

  public void setPosition(final String position)
  {
    this.position = position;
  }

  @Column(length = Constants.COMMENT_LENGTH)
  public String getComment()
  {
    return comment;
  }

  @Column(length = 255)
  public String getAbteilung()
  {
    return abteilung;
  }

  public void setAbteilung(final String abteilung)
  {
    this.abteilung = abteilung;
  }

  public void setComment(final String comment)
  {
    this.comment = comment;
  }
}
