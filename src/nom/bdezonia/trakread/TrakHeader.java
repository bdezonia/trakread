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
import java.io.IOException;
import java.io.PrintStream;

public class TrakHeader {

	String id_string = ""; // 6 chars: 1st 5 == TRACK
	short xDim;
	short yDim;
	short zDim;
	float xScale;
	float yScale;
	float zScale;
	float xOrigin;  // TrackVis might not use origin. Others may.
	float yOrigin;
	float zOrigin;
	short n_scalars;
	String[] scalarNames = new String[] {"","","","","","","","","",""};  // 10 of at most 20 chars each
	short n_properties;
	String[] propertyNames = new String[] {"","","","","","","","","",""};  // 10 of at most 20 chars each
	float[][] vox_to_ras = new float[][] {new float[4],new float[4],new float[4],new float[4]};  // 4 x 4 : if [3][3] == 0 then matrix is to be ignored
	byte[] reserved = new byte[444];  // 444 values
	char[] axis_order = new char[4];  // 4 values: stored as 1 byte ascii chars
	byte[] pad2 = new byte[4];  // 4 values: paddings
	float[] image_orientation_patient = new float[6];  // 6 values: from DICOM header
	byte[] pad1 = new byte[2];  // 2 values: paddings
	byte invert_x;  // inversion flag: internal use only
	byte invert_y;  // inversion flag: internal use only
	byte invert_z;  // inversion flag: internal use only
	byte swap_xy;  // rotation flag: internal use only
	byte swap_yz;  // rotation flag: internal use only
	byte swap_zx;  // rotation flag: internal use only
	int n_count;  // number of tracks in file: if 0 then just read until they are exhausted
	int version;  // version number: I based my code on version 2 as defined by trackvis people
	int hdr_size;  // use to determine byte swapping: should == 1000
	
	public TrakHeader() { }

	public void print(PrintStream stream) {
		
		stream.println("id string: " + id_string);
		stream.println("hdr size: " + hdr_size + " (if == 1000 no byte swapping needed)");
		stream.println("hdr version: " + version);
		stream.println("x dim: " + xDim);
		stream.println("y dim: " + yDim);
		stream.println("z dim: " + zDim);
		stream.println("x scale: " + xScale);
		stream.println("y scale: " + yScale);
		stream.println("z scale: " + zScale);
		stream.println("x offset: " + xOrigin);
		stream.println("y offset: " + yOrigin);
		stream.println("z offset: " + zOrigin);
		stream.println("n_scalars: " + n_scalars);
		for (int i = 0; i < Math.min(n_scalars, 10); i++) {
			stream.println("  scalar " + i + " name: "+ scalarNames[i]);
		}
		stream.println("n_properties: " + n_properties);
		for (int i = 0; i < Math.min(n_properties, 10); i++) {
			stream.println("  property " + i + " name: "+ propertyNames[i]);
		}
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				stream.println("matrix " + i + "," + j + ": " + vox_to_ras[i][j]);
			}
		}
		for (int i = 0; i < 4; i++) {
			stream.println("axis order "+i+": " + axis_order[i]);
		}
		for (int i = 0; i < 4; i++) {
			stream.println("pad2 "+i+": " + pad2[i]);
		}
		for (int i = 0; i < 6; i++) {
			stream.println("patient orientation " + i + ": " + image_orientation_patient[i]);
		}
		for (int i = 0; i < 2; i++) {
			stream.println("pad1 "+i+": " + pad1[i]);
		}
		stream.println("invert_x: " + invert_x);
		stream.println("invert_y: " + invert_y);
		stream.println("invert_z: " + invert_z);
		stream.println("swap_xy: " + swap_xy);
		stream.println("swap_yz: " + swap_yz);
		stream.println("swap_zx: " + swap_zx);
		stream.println("track count: " + n_count);
	}
	
	public static TrakHeader readFromSource(DataInput source, boolean dataIsLittleEndian) throws IOException {
		
		TrakHeader header = new TrakHeader();
		
		header.id_string = TrakUtils.readString(source, 6);
		header.xDim = TrakUtils.readShort(source, dataIsLittleEndian);
		header.yDim = TrakUtils.readShort(source, dataIsLittleEndian);
		header.zDim = TrakUtils.readShort(source, dataIsLittleEndian);
		header.xScale = TrakUtils.readFloat(source, dataIsLittleEndian);
		header.yScale = TrakUtils.readFloat(source, dataIsLittleEndian);
		header.zScale = TrakUtils.readFloat(source, dataIsLittleEndian);
		header.xOrigin = TrakUtils.readFloat(source, dataIsLittleEndian);
		header.yOrigin = TrakUtils.readFloat(source, dataIsLittleEndian);
		header.zOrigin = TrakUtils.readFloat(source, dataIsLittleEndian);
		header.n_scalars = TrakUtils.readShort(source, dataIsLittleEndian);
		for (int i = 0; i < 10; i++) {
			header.scalarNames[i] = TrakUtils.readString(source, 20);
		}
		header.n_properties = TrakUtils.readShort(source, dataIsLittleEndian);
		for (int i = 0; i < 10; i++) {
			header.propertyNames[i] = TrakUtils.readString(source, 20);
		}
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				header.vox_to_ras[i][j] = TrakUtils.readFloat(source, dataIsLittleEndian);
			}			
		}
		for (int i = 0; i < 444; i++) {
			header.reserved[i] = TrakUtils.readByte(source);
		}
		for (int i = 0; i < 4; i++) {
			header.axis_order[i] = (char)TrakUtils.readByte(source);
		}
		for (int i = 0; i < 4; i++) {
			header.pad2[i] = TrakUtils.readByte(source);
		}
		for (int i = 0; i < 6; i++) {
			header.image_orientation_patient[i] = TrakUtils.readFloat(source, dataIsLittleEndian);
		}
		for (int i = 0; i < 2; i++) {
			header.pad1[i] = TrakUtils.readByte(source);
		}
		header.invert_x = TrakUtils.readByte(source);
		header.invert_y = TrakUtils.readByte(source);
		header.invert_z = TrakUtils.readByte(source);
		header.swap_xy = TrakUtils.readByte(source);
		header.swap_yz = TrakUtils.readByte(source);
		header.swap_zx = TrakUtils.readByte(source);

		header.n_count = TrakUtils.readInt(source, dataIsLittleEndian);
		header.version = TrakUtils.readInt(source, dataIsLittleEndian);
		header.hdr_size = TrakUtils.readInt(source, dataIsLittleEndian);
		
		return header;
	}
}
