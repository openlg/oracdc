/**
 * Copyright (c) 2018-present, A2 Rešitve d.o.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See
 * the License for the specific language governing permissions and limitations under the License.
 */

package solutions.a2.cdc.oracle.internals;

import solutions.a2.oracle.internals.Xid;
import solutions.a2.oracle.utils.FormattingUtils;

/**
 * 
 * KRVMISC - LMNR xact finalize marker
 * 
 * Based on
 *     <a href="https://www.linkedin.com/in/julian-dyke-9a27837/">Julian Dyke</a> <a href="http://www.juliandyke.com/Presentations/RedoInternals.ppt">Redo Internals</a>
 *     <a href="https://www.linkedin.com/in/davidlitchfield/">David Litchfield</a> <a href="http://www.davidlitchfield.com/oracle_forensics_part_1._dissecting_the_redo_logs.pdf">Oracle Forensics Part 1: Dissecting the Redo Logs</a>
 *     <a href="https://www.linkedin.com/in/jure-kajzer-198a9a13/">Jure Kajzer</a> <a href="https://www.abakus.si/download/events/2014_jure_kajzer_forenzicna_analiza_oracle_log_datotek.pdf">Forensic analysis of Oracle log files</a>
 * 
 * @author <a href="mailto:averemee@a2.solutions">Aleksei Veremeev</a>
 *
 */

public class OraCdcChangeKrvMisc extends OraCdcChange {

	OraCdcChangeKrvMisc(final short num, final OraCdcRedoRecord redoRecord, final short operation, final byte[] record, final int offset, final int headerLength) {
		super(num, redoRecord, _24_4_MISC, record, offset, headerLength);
	}

	@Override
	StringBuilder toDumpFormat() {
		final StringBuilder sb = super.toDumpFormat();
		if (coords.length > 0x3 && 
				coords[0][1] > 0xA &&
				coords[1][1] > 0x3 &&
				coords[2][1] > 0x5 &&
				coords[3][1] > 0x7) {
			final int outcome = record[coords[1][0]];
			sb
			.append("\nLMNR xact finalize marker:  xid: ")
			.append((new Xid(
					redoLog.bu().getU16(record, coords[0][0] + 0x04),
					redoLog.bu().getU16(record, coords[0][0] + 0x06),
					redoLog.bu().getU32(record, coords[0][0] + 0x08))))
			.append(" outcome: ")
			.append(outcome)
			.append(" - ")
			.append(outcome == 1 ? "COMMIT" : "ABORT/ROLLBACK")
			.append("  start scn: 0x");
		FormattingUtils.leftPad(sb, redoLog.bu().getScn(record, coords[3][0]), 16);
		}
		return sb;
	}

	@Override
	public String toString() {
		return toDumpFormat().toString();
	}

}
