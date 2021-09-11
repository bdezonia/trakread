/*
 * TrakVis .TRK file reader and utilities
 * 
 * Written by Barry DeZonia during September 2021
 * 
 * Based on the TrackVis TRK file format as published here:
 * 
 *   http://www.trackvis.org/docs/?subsect=fileformat
 * 
 * This code is being put in the public domain. Use however you wish as freely as desired.
 * Just leave the author attribution and creation date information above and the disclaimer
 * below in any uses of the code. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL Barry DeZonia BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 * DAMAGE.
 * 
 */
package nom.bdezonia.trakread;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class TrakUtils {
	
	public static boolean fileIsLittleEndian(String filename) throws IOException  {
		
		File file = new File(filename);
		
		FileInputStream fis = new FileInputStream(file);
		
		DataInputStream dis = new DataInputStream(fis);
		
		boolean retVal = dataIsLittleEndian(dis);
		
		dis.close();
		
		return retVal;
	}

	/**
	 * NOTE: this routine assumes that the data source is at offset 0 (the beginning of the "stream").
	 * 
	 * @param source
	 * @return
	 * @throws IOException
	 */
	public static boolean dataIsLittleEndian(DataInput source) throws IOException {
		
		source.skipBytes(996);
		
		int val = source.readInt();
		
		return (val != 1000);
	}

	public static String readString(DataInput source, int maxChars) throws IOException {
		
		String s = "";
		
		boolean done = false;
		
		for (int i = 0; i < maxChars; i++) {
		
			byte ch = source.readByte();
			
			if (!done) {
			
				if (ch == 0)
				
					done = true;
				
				else
				
					s += (char) ch;
			}
		}
		
		return s;
	}

	public static byte readByte(DataInput source) throws IOException {
		
		return source.readByte();
	}
	
	public static short readShort(DataInput source, boolean dataIsLittleEndian) throws IOException {

		if (dataIsLittleEndian) {
		
			int b0 = source.readByte() & 0xff;
			int b1 = source.readByte() & 0xff;
			
			short value = (short) ((b1 << 8) | (b0 << 0));
			
			return value;
		}
		else {
			
			return source.readShort();
		}
	}

	public static int readInt(DataInput source, boolean dataIsLittleEndian) throws IOException {
		
		if (dataIsLittleEndian) {
		
			int b0 = source.readByte() & 0xff;
			int b1 = source.readByte() & 0xff;
			int b2 = source.readByte() & 0xff;
			int b3 = source.readByte() & 0xff;
			
			int intBits = (b3 << 24) | (b2 << 16) | (b1 << 8) | (b0 << 0);
			
			return intBits;
		}
		else {
			
			return source.readInt();
		}
	}

	public static float readFloat(DataInput source, boolean dataIsLittleEndian) throws IOException {
		
		if (dataIsLittleEndian) {
		
			int b0 = source.readByte() & 0xff;
			int b1 = source.readByte() & 0xff;
			int b2 = source.readByte() & 0xff;
			int b3 = source.readByte() & 0xff;
			
			int intBits = (b3 << 24) | (b2 << 16) | (b1 << 8) | (b0 << 0);
			
			return Float.intBitsToFloat(intBits);
		}
		else {
			
			return source.readFloat();
		}
	}
}
