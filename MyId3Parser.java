/* Copyright 2017 dice36d<dice36d@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

/* base on ID3 tag spec V2.4
 * resource: http://id3.org/id3v2.4.0-structure
 */

import java.io.File;
import java.io.FileInputStream;

public class MyId3Parser
{
	private static final int DEBUG_LEVEL = 2; // 0: silent excute; 1: error message allowed; 2: verbose

	private String mAlbum;
	private String mArtist;
	private String mYear;
	private String mTitle;

	private void errorMsg(String msg) {
		if (DEBUG_LEVEL > 0) {
			System.out.println("[Error] "+msg);
		}
	}

	private void debugMsg(String msg) {
		if (DEBUG_LEVEL == 2) {
			System.out.println("[Debug] " + msg);
		}
	}

	public MyId3Parser() {
	}

	public void parse(String path) {
		File pFile = new File(path);
		if (!pFile.exists()) {
			errorMsg("Can not open file(" + path + ")");
			return;
		}
		_parseV2(path);
	}

	private void _parseV2(String path) {
		debugMsg("parver v2 tags.");
		File mp3File = new File(path);
		FileInputStream fileInputStream = null;
		try {
			fileInputStream = new FileInputStream(mp3File);
		} catch(Exception e) {
			errorMsg("Failed to read from file, exits.");
			e.printStackTrace();
			return;
		}
		if (fileInputStream != null) {
			byte[] v2Header = new byte[10];
			try {
				int readSize = fileInputStream.read(v2Header);
				if (readSize < 10) {
					errorMsg("File \"" + path + "\" size to small");
					return;
				}
			} catch(Exception e) {
				errorMsg("File size too small");
				e.printStackTrace();
				return;
			}
			String id3v2Identifier = new String(v2Header, 0, 3);
			if (id3v2Identifier.compareTo("ID3") != 0) {
				errorMsg("Not found ID3 v2 tag");
				return;
			}
			int versionMain = (int) v2Header[3];
			int versionRevision = (int) v2Header[4];
			debugMsg(String.format("check v2 version main(%d) revision(%d)", versionMain, versionRevision));
			if (versionMain == 0xFF || versionRevision == 0xFF) {
				// Fail
				return;
			}
			byte v2Flags = v2Header[5];
			if (((int) v2Flags & 0x0F) > 0) {
				// Flags in low bits are not clear
				return;
			}
			boolean bUnsynchronisation = false;
			if (((int) v2Flags & 0x80) > 0) {
				bUnsynchronisation = true;
			}
			boolean bExtendedHeader = false;
			if (((int) v2Flags & 0x40) > 0) {
				bExtendedHeader = true;
			}
			boolean bExperimentalIndicator = false;
			if (((int) v2Flags & 0x20) > 0) {
				bExperimentalIndicator = true;
			}
			boolean bFooterPresent = false;
			if (((int) v2Flags & 0x10) > 0) {
				bFooterPresent = true;
			}
			debugMsg("Unsynchronisation(" + bUnsynchronisation +
				") Extended Header(" + bExtendedHeader +
				") Experimental Indicator(" + bExperimentalIndicator +
				") FooterPresent(" + bFooterPresent + ")");

//			int v2Size = int()
		}
	}

	private void _parseV1() {

	}

	public static void main(String[] args) {
		MyId3Parser parser = new MyId3Parser();
		parser.parse("test.mp3");
	}
}