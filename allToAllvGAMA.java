import javax.print.attribute.standard.RequestingUserName;

import mpi.*;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Arrays;

class test
{
    static int[] computeDispl(int tasks, int[] buffSendSize)
    {
        int[] displs = new int[tasks];
        displs[0] = 0;

        for(int index = 1; index < buffSendSize.length; index++)
        {
            displs[index] = displs[index-1] + buffSendSize[index-1];
        }
        return displs;
    }

	public static void test_all_to_all(int size)
	{
		
		try {
			int my_rank = MPI.COMM_WORLD.getRank();
			if(my_rank == 0)
			{
				System.out.println("-----------------------MPI_ALLTOALLV_test " + size);
			}
	        int world_size = MPI.COMM_WORLD.getSize(); // number of process in comm
	        
	        //System.out.println("my_rank : " + my_rank);
	        //System.out.println("world_size : " + world_size);
	        //System.out.println("msg number of elem  : " + size);
	        
	        int bufferReceiveSize[] = new int[world_size]; // buffer to receive size of incoming buffer in allToAllv
	        int buffSendSize[] = new int[world_size]; // buffer to send size of incoming buffer to all
	        
	        List<byte[]> serializedMessage = new ArrayList<byte[]>();
	        
	        for(int index = 0; index < world_size; index++)
	        {
	        	byte[] message = new byte[size];
		        Arrays.fill(message, (byte) my_rank);
	        		
        		buffSendSize[index] = message.length;
        		serializedMessage.add(message);
	        	
	        }
	         
	        byte[] finalMessage = new byte[Arrays.stream(buffSendSize).sum()];
	        int offset = 0;
	        for (byte[] byteArray : serializedMessage) {
	            System.arraycopy(byteArray, 0, finalMessage, offset, byteArray.length);
	            offset += byteArray.length;
	        }
	        
			//System.out.println("finalMessage lenght : "  +finalMessage.length);
	        
	        int displsSend[] = computeDispl(world_size, buffSendSize); // displs of send buffer
			//System.out.println("computeDispl displsSend ");
	
			for(var auto : buffSendSize)
	        {
	        	//System.out.println("buffSendSize " + auto);
	        }
			for(var auto : bufferReceiveSize)
	        {
	        	//System.out.println("bufferReceiveSize " + auto);
	        }
			
			//System.out.println("1st all to all ");
	        MPI.COMM_WORLD.allToAll(buffSendSize, 1, MPI.INT, bufferReceiveSize, 1, MPI.INT); // send to all + receive from all size of incoming buffer
	
			//System.out.println("bufferReceiveSize received : " + bufferReceiveSize.length);
			
	        int displsReceive[] = computeDispl(world_size, bufferReceiveSize); // displs of receive buffer*/
			//System.out.println("computeDispl displsReceive ");
	        byte bufferReceiveData[] = new byte[Arrays.stream(bufferReceiveSize).sum()]; // buffer to receive data
	

	        for(var auto : displsSend)
	        {
	        	//System.out.println("displsSend " + auto);
	        }
	        
	        for(var auto : displsReceive)
	        {
	        	//System.out.println("displsReceive " + auto);
	        }
	        
			//System.out.println("bufferReceiveData");
	        MPI.COMM_WORLD.allToAllv(finalMessage, buffSendSize, displsSend, MPI.BYTE, bufferReceiveData, bufferReceiveSize, displsReceive, MPI.BYTE); // send to all + receive from all with different size
	        
			//System.out.println("post allToAllv");
			

	        System.out.println("" + my_rank + " received : " + bufferReceiveData.length);

			MPI.COMM_WORLD.barrier();
			
		} catch (Exception e) {
			System.out.println("MPI_ALLTOALLV exception " + e);
			e.printStackTrace();
		} // rank of process
        
	}

    public static void main(String[] args) throws MPIException
    {        
        
		MPI.Init(args);

        int myself = MPI.COMM_WORLD.getRank(); // rank of process
        int tasks = MPI.COMM_WORLD.getSize(); // number of process in comm


		test_all_to_all(1750000);		// OK
		test_all_to_all(1500000);		// OK
		test_all_to_all(1000000);		// OK
		test_all_to_all(500000); 		// OK
		test_all_to_all(400000); 		// OK
		test_all_to_all(20000); 		// OK
	 	test_all_to_all(12500); 		// OK
	 	test_all_to_all(10000); 		// OK
		test_all_to_all(9000); 		// OK
		test_all_to_all(7500); 		// OK
		test_all_to_all(5000); 		// OK
		test_all_to_all(3500); 		// OK
		test_all_to_all(2000); 		// OK
		test_all_to_all(1000); 		// OK
		test_all_to_all(700);	 		// OK
		test_all_to_all(500);	 		// OK
		test_all_to_all(200);	 		// OK

        MPI.Finalize();
    }
}