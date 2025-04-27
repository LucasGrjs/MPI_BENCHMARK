import mpi.*;
import java.util.Arrays;
import java.util.Random;

class test {

    static int[] computeDispl(int tasks, int[] sizes) {
        int[] displs = new int[tasks];
        displs[0] = 0;
        for (int i = 1; i < sizes.length; i++) {
            displs[i] = displs[i - 1] + sizes[i - 1];
        }
        return displs;
    }

    public static void all_to_all(int size)
    {
        try {
            int my_rank = MPI.COMM_WORLD.getRank();
            int world_size = MPI.COMM_WORLD.getSize();

            if(my_rank == 0)
			{
				System.out.println("-----------------------MPI_ALLTOALL with " + world_size + " for " + size);
			}

            byte buffSend[] = new byte[world_size * size];
            int buffSendSize[] = new int[world_size];

            byte bufferReceive[] = new byte[world_size * size];

            for(int index = 0; index < world_size * size; index++)
            {
                buffSend[index] = (byte) my_rank;
            } 
            StringBuilder strSend = new StringBuilder();
            strSend.append("Rank " + my_rank + " will send " + size + " element(s)");

            System.out.println(strSend);

            for(int index = 0; index < world_size; index++)
            {
                buffSendSize[index] = size;   
            }

            MPI.COMM_WORLD.allToAll(buffSend, size, MPI.BYTE, bufferReceive, size, MPI.BYTE);
            
            System.out.println(my_rank + " received: " + bufferReceive.length + " bytes");
			MPI.COMM_WORLD.barrier();

        } catch (MPIException e) {
            System.err.println("MPI Error: " + e.getMessage());
            //MPI.Abort(MPI.COMM_WORLD, 1); // Exit with error code
        }
    }

    public static void all_to_all_V(int size) {
        try {
            int my_rank = MPI.COMM_WORLD.getRank();
            int world_size = MPI.COMM_WORLD.getSize();

			if(my_rank == 0)
			{
				System.out.println("-----------------------MPI_ALLTOALLV_test with " + world_size + " for " + size);
			}

            int[] send_sizes = new int[world_size];
            int[] recv_sizes = new int[world_size];

            // Generate random send sizes for each destination
            for (int i = 0; i < world_size; i++) {
                send_sizes[i] = size; // At least 1 element
            }

            int[] send_displs = computeDispl(world_size, send_sizes);
            int send_total = Arrays.stream(send_sizes).sum();
            byte[] send_buffer = new byte[send_total];

            // Fill send buffer with some data (replace with your actual data)
            for (int i = 0; i < send_total; i++) {
                send_buffer[i] = (byte) my_rank;
            }

            MPI.COMM_WORLD.barrier(); // Test barrier

            MPI.COMM_WORLD.allToAll(send_sizes, 1, MPI.INT, recv_sizes, 1, MPI.INT);

            MPI.COMM_WORLD.barrier(); // Test barrier

            int[] recv_displs = computeDispl(world_size, recv_sizes);
            int recv_total = Arrays.stream(recv_sizes).sum();
            byte[] recv_buffer = new byte[recv_total];

			System.out.println("Rank " + my_rank + " send_sizes: " + Arrays.toString(send_sizes));
			System.out.println("Rank " + my_rank + " send_displs: " + Arrays.toString(send_displs));
			System.out.println("Rank " + my_rank + " recv_sizes: " + Arrays.toString(recv_sizes));
			System.out.println("Rank " + my_rank + " recv_displs: " + Arrays.toString(recv_displs));

			System.out.println("Rank " + my_rank + " recv_buffer: " + recv_buffer.length);
			
            MPI.COMM_WORLD.barrier(); // Test barrier

			System.out.println("post barrier");

            MPI.COMM_WORLD.allToAllv(send_buffer, send_sizes, send_displs, MPI.BYTE, recv_buffer, recv_sizes, recv_displs, MPI.BYTE);

            MPI.COMM_WORLD.barrier(); // Test barrier

            System.out.println(my_rank + " received: " + recv_total + " bytes");
			MPI.COMM_WORLD.barrier();

        } catch (MPIException e) {
            System.err.println("MPI Error: " + e.getMessage());
            //MPI.Abort(MPI.COMM_WORLD, 1); // Exit with error code
        }
    }

    public static void main(String[] args) throws MPIException {
        MPI.Init(args);
        int my_rank = MPI.COMM_WORLD.getRank();
        int tasks = MPI.COMM_WORLD.getSize();

        all_to_all_V(1); // Example max size
        all_to_all_V(10); // Example max size
        all_to_all_V(5); // Example max size
        all_to_all_V(10); // Example max size
        all_to_all_V(20); // Example max size
        all_to_all_V(50); // Example max size
        all_to_all_V(100); // Example max size
        all_to_all_V(150); // Example max size
        all_to_all_V(200); // Example max size
        all_to_all_V(500); // Example max size
        all_to_all_V(1000); // Example max size
        all_to_all_V(2000); // Example max size
        all_to_all_V(5000); // Example max size
        all_to_all_V(7500); // Example max size
        all_to_all_V(10000); // Example max size
        all_to_all_V(15000); // Example max size
        all_to_all_V(20000); // Example max size

        all_to_all_V(23170); // Example max size
        //all_to_all_V(25000); // Example max size

        /*all_to_all(1); // Example max size
        all_to_all(10); // Example max size
        all_to_all(5); // Example max size
        all_to_all(10); // Example max size
        all_to_all(20); // Example max size
        all_to_all(50); // Example max size
        all_to_all(100); // Example max size
        all_to_all(150); // Example max size
        all_to_all(200); // Example max size
        all_to_all(500); // Example max size
        all_to_all(1000); // Example max size
        all_to_all(2000); // Example max size
        all_to_all(5000); // Example max size
        all_to_all(7500); // Example max size
        all_to_all(10000); // Example max size
        all_to_all(15000); // Example max size
        all_to_all(20000); // Example max size
        all_to_all(25000); // Example max size
		all_to_all(50000); // Example max size
		all_to_all(75000); // Example max size
		all_to_all(100000); // Example max size
		all_to_all(200000); // Example max size
		all_to_all(500000); // Example max size
		all_to_all(1000000); // Example max size
		all_to_all(10000000); // Example max size*/

		
        
		//all_to_all_V(20000); // Example max size
		//all_to_all_V(25000); // OK
		//all_to_all_V(30000); // OK
		//all_to_all_V(40000); 		// OK
		//all_to_all_V(50000); 		// OK
		//all_to_all_V(75000);		// OK
		//all_to_all_V(100000);		// OK
		//all_to_all_V(50000); // NOK
		
		/*all_to_all_V(100000); // NOK
		all_to_all_V(200000); // NOK
		all_to_all_V(250000); // NOK
		all_to_all_V(300000); // NOK
		all_to_all_V(350000); // NOK
		all_to_all_V(350000000); // NOK*/

		//all_to_all_V(175000);		// OK
		//all_to_all_V(150000);		// OK
		//all_to_all_V(100000);		// OK

        MPI.Finalize();
    }
}