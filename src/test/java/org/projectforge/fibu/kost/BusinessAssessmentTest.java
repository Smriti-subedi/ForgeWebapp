/////////////////////////////////////////////////////////////////////////////
//
// Project ProjectForge Community Edition
//         www.projectforge.org
//
// Copyright (C) 2001-2012 Kai Reinhard (k.reinhard@micromata.com)
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

package org.projectforge.fibu.kost;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.projectforge.fibu.KontoDO;

public class BusinessAssessmentTest
{
  @Test
  public void testCalculations()
  {
    // <row no='1220' id='kostenWarenabgabe' accountRange='6740' priority='low' title='Kosten Warenabgabe' />
    // <row no='1260' id='sonstigeKosten' accountRange='6300,6800-6855' priority='low' title='sonstige Kosten' />

    final BusinessAssessmentConfig bwaConfig = BusinessAssessmentConfigTest.getBusinessAssessmentConfig();
    final List<BuchungssatzDO> records = new ArrayList<BuchungssatzDO>();
    records.add(createRecord(-1.01, 6740));
    records.add(createRecord(-2.02, 6740));
    records.add(createRecord(-1.01, 6300));
    records.add(createRecord(-2.02, 6800));
    records.add(createRecord(-4.04, 6855));
    records.add(createRecord(-8.08, 6805));
    final BusinessAssessment bwa = new BusinessAssessment(bwaConfig, records);
    assertEquals(new BigDecimal("-3.03"), bwa.getRow("1220").getAmount());
    assertEquals(new BigDecimal("-15.15"), bwa.getRow("1260").getAmount());
  }

  private BuchungssatzDO createRecord(final double amount, final int accountNumber)
  {
    final BuchungssatzDO record = new BuchungssatzDO();
    final KontoDO konto = new KontoDO();
    konto.setNummer(accountNumber);
    record.setKonto(konto);
    record.setBetrag(new BigDecimal(amount));
    return record;
  }
}
