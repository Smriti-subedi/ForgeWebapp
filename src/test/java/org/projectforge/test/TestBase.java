/////////////////////////////////////////////////////////////////////////////
//
// Project ProjectForge Community Edition
//         www.projectforge.org
//
// Copyright (C) 2001-2011 Kai Reinhard (k.reinhard@me.com)
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

package org.projectforge.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.hibernate.Query;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.projectforge.access.AccessChecker;
import org.projectforge.access.AccessException;
import org.projectforge.access.AccessType;
import org.projectforge.access.OperationType;
import org.projectforge.common.DateHelper;
import org.projectforge.core.SimpleHistoryEntry;
import org.projectforge.task.TaskDO;
import org.projectforge.user.GroupDO;
import org.projectforge.user.PFUserContext;
import org.projectforge.user.PFUserDO;
import org.projectforge.user.ProjectForgeGroup;
import org.projectforge.user.UserDao;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import de.micromata.hibernate.history.HistoryEntry;
import de.micromata.hibernate.history.HistoryEntryType;
import de.micromata.hibernate.history.delta.PropertyDelta;

/**
 * @author Kai Reinhard (k.reinhard@micromata.de)
 * 
 */
public class TestBase
{
  private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(TestBase.class);

  public static final String TEST_DIR = "/tmp/ProjectForgeTests"; // If you change this, don't forget to change it in

  // test-applicationContext.xml too.

  public static final String ADMIN = "PFAdmin";

  public static final String TEST_ADMIN_USER = "testSysAdmin";

  public static final String TEST_FINANCE_USER = "testFinanceUser";

  public static final String TEST_GROUP = "testGroup";

  public static final String TEST_USER = "testUser";

  public static final String TEST_USER2 = "testUser2";

  public static final String TEST_PROJECT_MANAGER_USER = "testProjectManager";

  public static final String TEST_PROJECT_ASSISTANT_USER = "testProjectAssistant";

  public static final String TEST_CONTROLLING_USER = "testController";

  public static final String TEST_MARKETING_USER = "testMarketingUser";

  public static final String ADMIN_GROUP = ProjectForgeGroup.ADMIN_GROUP.toString();

  public static final String FINANCE_GROUP = ProjectForgeGroup.FINANCE_GROUP.toString();

  public static final String CONTROLLING_GROUP = ProjectForgeGroup.CONTROLLING_GROUP.toString();

  public static final String PROJECT_MANAGER = ProjectForgeGroup.PROJECT_MANAGER.toString();

  public static final String PROJECT_ASSISTANT = ProjectForgeGroup.PROJECT_ASSISTANT.toString();

  public static final String MARKETING_GROUP = ProjectForgeGroup.MARKETING_GROUP.toString();

  public static final String ORGA_GROUP = ProjectForgeGroup.ORGA_TEAM.toString();

  private static TestConfiguration configuration;

  static PFUserDO ADMIN_USER;

  protected HibernateTemplate hibernate;

  protected UserDao userDao;
  
  protected AccessChecker accessChecker;

  protected static InitTestDB initTestDB;

  protected int mCount = 0;

  protected static TestConfiguration getConfiguration()
  {
    return configuration;
  }

  /**
   * Init and reinitialise context before each run
   */
  protected static void init(boolean createTestData) throws BeansException, IOException
  {
    TimeZone.setDefault(DateHelper.UTC);
    log.info("user.timezone is: " + System.getProperty("user.timezone"));
    TestConfiguration.initAsTestConfiguration();
    configuration = TestConfiguration.getConfiguration();
    initTestDB = configuration.getBean("initTestDB", InitTestDB.class);
    configuration.autowire(initTestDB, AutowireCapableBeanFactory.AUTOWIRE_BY_NAME);
    final File testDir = new File(TEST_DIR);
    if (testDir.exists() == false) {
      testDir.mkdir();
    }

    clearDatabase();

    if (createTestData == true) {
      initTestDB.initDatabase();
    }
  }

  public static InitTestDB getInitTestDB()
  {
    return initTestDB;
  }

  @Before
  public void initTest()
  {
    userDao = configuration.getBean("userDao", UserDao.class);
    configuration.autowire(this, AutowireCapableBeanFactory.AUTOWIRE_BY_NAME);
    configuration.autowire(this, AutowireCapableBeanFactory.AUTOWIRE_BY_NAME);
  }

  protected static void clearDatabase()
  {
    PFUserContext.setUser(ADMIN_USER); // Logon admin user.
    TransactionTemplate transactionTemplate = configuration.getBean("txTemplate", TransactionTemplate.class);
    final HibernateTemplate hibernateTemplate = configuration.getBean("hibernate", HibernateTemplate.class);
    transactionTemplate.execute(new TransactionCallback() {
      public Object doInTransaction(TransactionStatus status)
      {
        deleteFrom(hibernateTemplate, "TimesheetDO");
        deleteFrom(hibernateTemplate, "HRPlanningEntryDO");
        deleteFrom(hibernateTemplate, "HRPlanningDO");
        deleteFrom(hibernateTemplate, "AccessEntryDO");
        deleteFrom(hibernateTemplate, "PersonalAddressDO");
        deleteFrom(hibernateTemplate, "AddressDO");
        deleteFrom(hibernateTemplate, "RechnungsPositionDO"); // Before Autrag*DO
        deleteFrom(hibernateTemplate, "RechnungDO");
        deleteFrom(hibernateTemplate, "AuftragsPositionDO");
        deleteFrom(hibernateTemplate, "AuftragDO");
        deleteFrom(hibernateTemplate, "BookDO");
        deleteFrom(hibernateTemplate, "BuchungssatzDO");
        deleteFrom(hibernateTemplate, "ConfigurationDO");
        deleteFrom(hibernateTemplate, "EingangsrechnungsPositionDO");
        deleteFrom(hibernateTemplate, "EingangsrechnungDO");
        deleteFrom(hibernateTemplate, "EmployeeDO");
        deleteFrom(hibernateTemplate, "EmployeeSalaryDO");
        deleteFrom(hibernateTemplate, "KontoDO");
        deleteFrom(hibernateTemplate, "Kost1DO");
        deleteFrom(hibernateTemplate, "Kost2DO");
        deleteFrom(hibernateTemplate, "Kost2ArtDO");
        deleteFrom(hibernateTemplate, "KostZuweisungDO");
        deleteFrom(hibernateTemplate, "GroupTaskAccessDO");
        deleteAllDBObjects(hibernateTemplate, "ProjektDO"); // Before task
        deleteFrom(hibernateTemplate, "KundeDO");
        deleteFrom(hibernateTemplate, "GanttChartDO"); // Before task
        deleteAllDBObjects(hibernateTemplate, "TaskDO");
        deleteAllDBObjects(hibernateTemplate, "MebEntryDO");
        deleteFrom(hibernateTemplate, "ImportedMebEntryDO");
        deleteFrom(hibernateTemplate, "ScriptDO");
        deleteFrom(hibernateTemplate, "UserPrefEntryDO");
        deleteFrom(hibernateTemplate, "UserPrefDO");
        deleteFrom(hibernateTemplate, "UserRightDO");
        deleteFrom(hibernateTemplate, "UserXmlPreferencesDO");
        deleteAllDBObjects(hibernateTemplate, "GroupDO");
        deleteAllDBObjects(hibernateTemplate, "PFUserDO");
        deleteFrom(hibernateTemplate, "de.micromata.hibernate.history.delta.PropertyDelta");
        deleteFrom(hibernateTemplate, "de.micromata.hibernate.history.HistoryEntry");
        List< ? > all = hibernateTemplate.find("from java.lang.Object o");
        if (all != null && all.size() > 0) {
          all = hibernateTemplate.find("from java.lang.Object o");
          assertEquals("Database should be empty", 0, all.size());
          configuration.setInitialized(false);
          // logEnd();
        }
        return null;
      }
    });
  }

  private static void deleteFrom(final HibernateTemplate hibernateTemplate, String entity)
  {
    final Query query = hibernateTemplate.getSessionFactory().getCurrentSession().createQuery("delete from " + entity);
    query.executeUpdate();
    hibernateTemplate.flush();
  }

  private static void deleteAllDBObjects(final HibernateTemplate hibernateTemplate, String entity)
  {
    final List< ? > all = hibernateTemplate.find("from " + entity + " o");
    if (all != null && all.size() > 0) {
      hibernateTemplate.deleteAll(all);
      hibernateTemplate.flush();
    }
  }

  protected PFUserDO logon(String username)
  {
    PFUserDO user = userDao.getInternalByName(username);
    if (user == null) {
      fail("User not found: " + username);
    }
    PFUserContext.setUser(user);
    return user;
  }

  protected void logon(PFUserDO user)
  {
    PFUserContext.setUser(user);
  }

  protected void logoff()
  {
    PFUserContext.setUser(null);
  }

  /**
   * spring accessor
   * 
   * @param hibernate
   */
  public void setHibernate(HibernateTemplate hibernate)
  {
    this.hibernate = hibernate;
  }

  public HibernateTemplate getHibernateTemplate()
  {
    return this.hibernate;
  }

  /**
   * spring accessor
   * 
   * @param userDao
   */
  public void setUserDao(UserDao userDao)
  {
    this.userDao = userDao;
  }
  
  public void setAccessChecker(AccessChecker accessChecker)
  {
    this.accessChecker = accessChecker;
  }

  @BeforeClass
  public static void setUp() throws Exception
  {
    init(true);
  }

  @AfterClass
  public static void shutdown()
  {
    clearDatabase();
    deleteDB();
  }

  protected static void deleteDB()
  {
    String databaseUrl = configuration.getDatabaseUrl();
    String baseFilename = databaseUrl.substring(databaseUrl.lastIndexOf(':') + 1);
    File data = new File(baseFilename + ".data");
    if (data.exists() == true) {
      System.out.println("Deleting database files (" + baseFilename + ".*)");
      deleteFile(baseFilename + ".backup");
      deleteFile(baseFilename + ".data");
      deleteFile(baseFilename + ".lck");
      deleteFile(baseFilename + ".log");
      deleteFile(baseFilename + ".properties");
      deleteFile(baseFilename + ".script");
    }
  }

  private static void deleteFile(String filename)
  {
    File file = new File(filename);
    if (file.canRead()) {
      file.delete();
    }
  }

  public GroupDO getGroup(String groupName)
  {
    return initTestDB.getGroup(groupName);
  }

  public Integer getGroupId(String groupName)
  {
    return initTestDB.getGroup(groupName).getId();
  }

  public TaskDO getTask(String taskName)
  {
    return initTestDB.getTask(taskName);
  }

  public PFUserDO getUser(String userName)
  {
    return initTestDB.getUser(userName);
  }

  public Integer getUserId(String userName)
  {
    return initTestDB.getUser(userName).getId();
  }

  protected void logStart(String name)
  {
    logStartPublic(name);
    mCount = 0;
  }

  protected void logEnd()
  {
    logEndPublic();
    mCount = 0;
  }

  protected void logDot()
  {
    log(".");
  }

  protected void log(String string)
  {
    logPublic(string);
    if (++mCount % 40 == 0) {
      System.out.println("");
    }
  }

  public static void logStartPublic(String name)
  {
    System.out.print(name + ": ");
  }

  public static void logEndPublic()
  {
    System.out.println(" (OK)");
  }

  public static void logDotPublic()
  {
    logPublic(".");
  }

  public static void logPublic(String string)
  {
    System.out.print(string);
  }

  public static void logSingleEntryPublic(String string)
  {
    System.out.println(string);
  }

  protected void assertAccessException(AccessException ex, Integer taskId, AccessType accessType, OperationType operationType)
  {
    assertEquals(accessType, ex.getAccessType());
    assertEquals(operationType, ex.getOperationType());
    assertEquals(taskId, ex.getTaskId());
  }

  protected void assertHistoryEntry(HistoryEntry entry, Integer entityId, PFUserDO user, HistoryEntryType type)
  {
    assertHistoryEntry(entry, entityId, user, type, null, null, null, null);
  }

  protected void assertHistoryEntry(HistoryEntry entry, Integer entityId, PFUserDO user, HistoryEntryType type, String propertyName,
      Class< ? > classType, Object oldValue, Object newValue)
  {
    assertEquals(user.getId().toString(), entry.getUserName());
    // assertEquals(AddressDO.class.getSimpleName(), entry.getClassName());
    assertEquals(null, entry.getComment());
    assertEquals(type, entry.getType());
    assertEquals(entityId, entry.getEntityId());
    if (propertyName != null) {
      List<PropertyDelta> delta = entry.getDelta();
      assertEquals(1, delta.size());
      PropertyDelta prop = delta.get(0);
      assertPropertyDelta(prop, propertyName, classType, oldValue, newValue);
    }
  }

  protected void assertPropertyDelta(PropertyDelta prop, String propertyName, Class< ? > propertyType, Object oldValue, Object newValue)
  {
    assertEquals(propertyName, prop.getPropertyName());
    assertEquals(oldValue.toString(), prop.getOldValue());
    assertEquals(newValue.toString(), prop.getNewValue());
    if (propertyType.getName().equals(prop.getPropertyType()) == false) {
      assertEquals(propertyType.getSimpleName(), prop.getPropertyType());
    }
  }

  protected void assertSimpleHistoryEntry(SimpleHistoryEntry entry, PFUserDO user, HistoryEntryType entryType, String propertyName,
      Class< ? > propertyType, Object oldValue, Object newValue)
  {
    assertEquals(user.getId(), entry.getUser().getId());
    assertEquals(entryType, entry.getEntryType());
    if (propertyName != null) {
      assertEquals(propertyName, entry.getPropertyName());
      if (propertyType.getName().equals(entry.getPropertyType()) == false) {
        assertEquals(propertyType.getSimpleName(), entry.getPropertyType());
      }
      assertEquals(oldValue.toString(), entry.getOldValue());
      assertEquals(newValue.toString(), entry.getNewValue());
    }
  }

  protected void assertBigDecimal(final int v1, final BigDecimal v2)
  {
    assertBigDecimal(new BigDecimal(v1), v2);
  }

  protected void assertBigDecimal(final BigDecimal v1, final BigDecimal v2)
  {
    assertTrue("BigDecimal values are not equal.", v1.compareTo(v2) == 0);
  }

  protected Calendar assertUTCDate(final Date date, final int year, final int month, final int day, final int hour, final int minute,
      final int second)
  {
    final Calendar cal = Calendar.getInstance(DateHelper.UTC);
    cal.setTime(date);
    assertEquals(year, cal.get(Calendar.YEAR));
    assertEquals(month, cal.get(Calendar.MONTH));
    assertEquals(day, cal.get(Calendar.DAY_OF_MONTH));
    assertEquals(hour, cal.get(Calendar.HOUR_OF_DAY));
    assertEquals(minute, cal.get(Calendar.MINUTE));
    assertEquals(second, cal.get(Calendar.SECOND));
    return cal;
  }

  protected Calendar assertUTCDate(final Date date, final int year, final int month, final int day, final int hour, final int minute,
      final int second, final int millis)
  {
    final Calendar cal = assertUTCDate(date, year, month, day, hour, minute, second);
    assertEquals(millis, cal.get(Calendar.MILLISECOND));
    return cal;
  }
}
