/////////////////////////////////////////////////////////////////////////////
//
// Project ProjectForge Community Edition
//         www.projectforge.org
//
// Copyright (C) 2001-2012 Kai Reinhard (k.reinhard@micromata.de)
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

package org.projectforge.plugins.teamcal.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.projectforge.common.DateHolder;
import org.projectforge.plugins.teamcal.admin.TeamCalDO;
import org.projectforge.plugins.teamcal.admin.TeamCalsProvider;
import org.projectforge.web.wicket.AbstractListPage;
import org.projectforge.web.wicket.CellItemListener;
import org.projectforge.web.wicket.CellItemListenerPropertyColumn;
import org.projectforge.web.wicket.DetachableDOModel;
import org.projectforge.web.wicket.IListPageColumnsCreator;
import org.projectforge.web.wicket.ListPage;
import org.projectforge.web.wicket.ListSelectActionPanel;
import org.projectforge.web.wicket.WicketUtils;

/**
 * @author Kai Reinhard (k.reinhard@micromata.de)
 * 
 */
@ListPage(editPage = TeamEventEditPage.class)
public class TeamEventListPage extends AbstractListPage<TeamEventListForm, TeamEventDao, TeamEventDO> implements
IListPageColumnsCreator<TeamEventDO>
{
  private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(TeamEventListPage.class);

  public static final String PARAM_CALENDARS = "cals";

  private static final long serialVersionUID = 1749480610890950450L;

  @SpringBean(name = "teamEventDao")
  private TeamEventDao teamEventDao;

  /**
   * 
   */
  public TeamEventListPage(final PageParameters parameters)
  {
    super(parameters, "plugins.teamevent");
  }

  protected void onFormInit()
  {
    final String str = WicketUtils.getAsString(getPageParameters(), PARAM_CALENDARS);
    if (StringUtils.isNotBlank(str) == true) {
      final Collection<TeamCalDO> teamCals = new TeamCalsProvider().getSortedCalendars(str);
      getFilter().setTeamCals(teamCals);
    }
  }

  /**
   * @see org.projectforge.web.wicket.IListPageColumnsCreator#createColumns(org.apache.wicket.markup.html.WebPage, boolean)
   */
  @SuppressWarnings("serial")
  @Override
  public List<IColumn<TeamEventDO>> createColumns(final WebPage returnToPage, final boolean sortable)
  {
    final List<IColumn<TeamEventDO>> columns = new ArrayList<IColumn<TeamEventDO>>();

    final CellItemListener<TeamEventDO> cellItemListener = new CellItemListener<TeamEventDO>() {
      public void populateItem(final Item<ICellPopulator<TeamEventDO>> item, final String componentId, final IModel<TeamEventDO> rowModel)
      {
        final TeamEventDO teamEvent = rowModel.getObject();
        final StringBuffer cssStyle = getCssStyle(teamEvent.getId(), teamEvent.isDeleted());
        if (cssStyle.length() > 0) {
          item.add(AttributeModifier.append("style", new Model<String>(cssStyle.toString())));
        }
      }
    };

    columns.add(new CellItemListenerPropertyColumn<TeamEventDO>(getString("plugins.teamcal.calendar"), null, "calendar", cellItemListener) {
      /**
       * @see org.projectforge.web.wicket.CellItemListenerPropertyColumn#populateItem(org.apache.wicket.markup.repeater.Item,
       *      java.lang.String, org.apache.wicket.model.IModel)
       */
      @Override
      public void populateItem(final Item<ICellPopulator<TeamEventDO>> item, final String componentId, final IModel<TeamEventDO> rowModel)
      {
        final TeamEventDO teamEvent = rowModel.getObject();
        final StringBuffer cssStyle = getCssStyle(teamEvent.getId(), teamEvent.isDeleted());
        if (cssStyle.length() > 0) {
          item.add(AttributeModifier.append("style", new Model<String>(cssStyle.toString())));
        }
        final TeamCalDO calendar = teamEvent.getCalendar();
        item.add(new ListSelectActionPanel(componentId, rowModel, TeamEventEditPage.class, teamEvent.getId(), returnToPage,
            calendar != null ? calendar.getTitle() : ""));
        addRowClick(item);
      }
    });

    columns.add(new CellItemListenerPropertyColumn<TeamEventDO>(getString("plugins.teamevent.subject"), getSortable("subject", sortable),
        "subject", cellItemListener));
    columns.add(new CellItemListenerPropertyColumn<TeamEventDO>(getString("plugins.teamevent.beginDate"),
        getSortable("startDate", sortable), "startDate", cellItemListener));
    columns.add(new CellItemListenerPropertyColumn<TeamEventDO>(getString("plugins.teamevent.endDate"), getSortable("endDate", sortable),
        "endDate", cellItemListener));
    columns.add(new CellItemListenerPropertyColumn<TeamEventDO>(getString("plugins.teamevent.allDay"), getSortable("allDay", sortable),
        "allDay", cellItemListener) {
      @Override
      public void populateItem(final Item<ICellPopulator<TeamEventDO>> item, final String componentId, final IModel<TeamEventDO> rowModel)
      {
        final TeamEventDO event = rowModel.getObject();
        final Label label = WicketUtils.createBooleanLabel(getResponse(), componentId, event.isAllDay() == true);
        item.add(label);
        cellItemListener.populateItem(item, componentId, rowModel);
      }
    });
    return columns;
  }

  /**
   * @see org.projectforge.web.wicket.AbstractListPage#onSearchSubmit()
   */
  @Override
  protected boolean onSearchSubmit()
  {
    getFilter().setTeamCals(form.calendarsListHelper.getAssignedItems());
    return super.onSearchSubmit();
  }

  /**
   * @see org.projectforge.web.wicket.AbstractListPage#select(java.lang.String, java.lang.Object)
   */
  @Override
  public void select(final String property, final Object selectedValue)
  {
    if (property.startsWith("quickSelect.") == true) { // month".equals(property) == true) {
      final Date date = (Date) selectedValue;
      form.getSearchFilter().setStartDate(date);
      final DateHolder dateHolder = new DateHolder(date);
      if (property.endsWith(".month") == true) {
        dateHolder.setEndOfMonth();
      } else if (property.endsWith(".week") == true) {
        dateHolder.setEndOfWeek();
      } else {
        log.error("Property '" + property + "' not supported for selection.");
      }
      form.getSearchFilter().setEndDate(dateHolder.getDate());
      form.startDate.markModelAsChanged();
      form.endDate.markModelAsChanged();
      refresh();
    } else {
      super.select(property, selectedValue);
    }
  }

  protected TeamEventFilter getFilter()
  {
    return form.getFilter();
  }

  /**
   * @see org.projectforge.web.wicket.AbstractListPage#getBaseDao()
   */
  @Override
  protected TeamEventDao getBaseDao()
  {
    return teamEventDao;
  }

  /**
   * @see org.projectforge.web.wicket.AbstractListPage#newListForm(org.projectforge.web.wicket.AbstractListPage)
   */
  @Override
  protected TeamEventListForm newListForm(final AbstractListPage< ? , ? , ? > parentPage)
  {
    return new TeamEventListForm(this);
  }

  /**
   * @see org.projectforge.web.wicket.AbstractListPage#getModel(java.lang.Object)
   */
  @Override
  protected IModel<TeamEventDO> getModel(final TeamEventDO object)
  {
    final DetachableDOModel<TeamEventDO, TeamEventDao> model = new DetachableDOModel<TeamEventDO, TeamEventDao>(object, getBaseDao());
    return model;
  }

  /**
   * @see org.projectforge.web.wicket.AbstractListPage#init()
   */
  @Override
  protected void init()
  {
    dataTable = createDataTable(createColumns(this, true), "lastUpdate", SortOrder.DESCENDING);
    form.add(dataTable);
  }
}
