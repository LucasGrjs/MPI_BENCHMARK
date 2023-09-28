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

class test
{
    // allGather, every proc gather from every proc the same number of data
    // https://www.mpich.org/static/docs/v3.2/www3/MPI_Allgatherv.html
    public static void main(String[] args) throws MPIException
    {        
        MPI.Init(args);
        
        int numberOfIntToSend = 7; // number of int to send to others
        int myself = MPI.COMM_WORLD.getRank(); // rank of current mpi instance
        int tasks = MPI.COMM_WORLD.getSize(); // number of process

        int receive[] = new int[numberOfIntToSend * tasks]; // buffer to receive all data from others
        int send[] = new int[numberOfIntToSend]; // buffer to send all data to others

        int targetRank = 2; // rank to send data to
        if(targetRank >= tasks)
        {
            targetRank = 0;
        }

        StringBuilder strIn = new StringBuilder("Rank " + myself + " will send " + numberOfIntToSend + " element(s) to rank " + targetRank + " : ");
        for(int index = 0; index < send.length; index++)
        {
            send[index] = myself; // data to send
            strIn.append(send[index] + " ");
        }
        System.out.println(strIn + "\n");

        MPI.COMM_WORLD.allGather(send, numberOfIntToSend, MPI.INT, receive, numberOfIntToSend, MPI.INT); // receive + send
        
        StringBuilder strOut = new StringBuilder("Rank " + myself + " received : ");
        for(int index = 0; index < receive.length; index++)
        {
            strOut.append(receive[index] + " ");
        }
        System.out.println(strOut + "\n");

        MPI.Finalize();
    }
}