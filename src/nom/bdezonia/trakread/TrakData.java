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

public class TrakData {

	// This code shows anyone who wants to compile the input data into something a proper
	// way of going about it.
	
	public static void printSummary(PrintStream stream, DataInput source, boolean dataIsLittleEndian, TrakHeader header) throws IOException {
		
		stream.println("Scanning all tracks for summarization. This may take a while ...");
		
		final int DIFFERENCES_EXIST = -5000;
		
		int numScalarsPerPoint = header.n_scalars + 3;
		
		int numPropertiesPerTrack = header.n_properties;
		
		int tracksSoFar = 0;
		
		int trackSize = -1;
		
		while (true) {
			try {
		
				// read a track
				
				int numPointsInTrack = TrakUtils.readInt(source, dataIsLittleEndian);
				
				if (trackSize == -1) {
				
					trackSize = numPointsInTrack;
				}
				else {
				
					if (trackSize != DIFFERENCES_EXIST && numPointsInTrack != trackSize)
					
						trackSize = DIFFERENCES_EXIST;
				}
				
				// read the track a point at a time
				
				for (int i = 0; i < numPointsInTrack; i++) {
					
					for (int j = 0; j < numScalarsPerPoint; j++) {
					
						// throwing away data for now
				
						TrakUtils.readFloat(source, dataIsLittleEndian);
					}
				}
				
				for (int j = 0; j < numPropertiesPerTrack; j++) {
					
					// throwing away data for now
					
					TrakUtils.readFloat(source, dataIsLittleEndian);
				}
				
				// stream.println("Just read track "+tracksSoFar);
				
				tracksSoFar++;
				
			} catch (Exception e) {

				if (trackSize == DIFFERENCES_EXIST) {
					
					stream.println("Source has a varying number of points per track.");
				}
				else {
					
					stream.println("Source has a fixed number of points per track: " + trackSize);
				}

				System.out.println("Scanned "+tracksSoFar+" tracks.");
			}
		}
	}
}
