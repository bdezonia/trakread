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
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import nom.bdezonia.trakread.TrakData;
import nom.bdezonia.trakread.TrakHeader;
import nom.bdezonia.trakread.TrakUtils;

public class Main {

	public static void main(String[] args) {
		
		String filename = "/home/bdezonia/images/nifti/DTI_Lab3_tracts.trk";
		
		boolean fileIsLittleEndian = false;
		
		try {
			
			fileIsLittleEndian = TrakUtils.fileIsLittleEndian(filename);
		
		} catch (IOException e) {
		
			System.err.println("EXITING: AN IO EXCEPTION OCCURRED: " + e.getMessage());

			System.exit(1);  // return error condition
		}
		
		File file = new File(filename);
		
		FileInputStream fileStream = null;
		
		try {
		
			fileStream = new FileInputStream(file);
		
		} catch (FileNotFoundException e) {
		
			System.err.println("EXITING: FILE NOT FOUND: " + filename);
			
			System.exit(2);  // return error condition
		}
		
		DataInputStream dataStream = new DataInputStream(fileStream);

		TrakHeader header = new TrakHeader();
		
		try {
		
			header = TrakHeader.readFromSource(dataStream, fileIsLittleEndian);
			
			// report header vals
			
			header.print(System.out);
			
			// and then read data if desired

			TrakData.printSummary(System.out, dataStream, fileIsLittleEndian, header);
			
			dataStream.close();
			
		} catch (IOException e) {

			System.err.println("EXITING: COULD NOT READ FILE HEADER: " + e.getMessage());
			
			System.exit(3);  // return error condition
		}
		
		// let OS know things are great
		
		System.exit(0);
	}
}
