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
    // allToall, every proc send sizeEach element to other proc
    public static void main(String[] args) throws MPIException
    {        
        MPI.Init(args);

        Random rand = new Random();

        int myself = MPI.COMM_WORLD.getRank(); // rank
        int tasks = MPI.COMM_WORLD.getSize(); // number of process
        int sizeEach = 3;   

        int buffSend[] = new int[tasks * sizeEach];
        int buffSendSize[] = new int[tasks];

        int bufferReceive[] = new int[tasks * sizeEach];
        buffSendSize[0] = sizeEach;

        for(int index = 0; index < tasks * sizeEach; index++)
        {
            buffSend[index] = myself;
        } 
        StringBuilder strSend = new StringBuilder();
        strSend.append("buffSend myself : " + myself + " ");
        for(int index = 1; index < tasks * sizeEach; index++)
        {
            strSend.append(" " + buffSend[index]);
        }
        System.out.println(strSend);

        for(int index = 1; index < tasks; index++)
        {
            buffSendSize[index] = sizeEach;   
        }
        MPI.COMM_WORLD.allToAll(buffSend, sizeEach, MPI.INT, bufferReceive, sizeEach, MPI.INT);
        
        StringBuilder str = new StringBuilder();
        str.append("bufferReceive myself : " + myself + " ");
        for(int index = 0; index < tasks * sizeEach; index++)
        {
            str.append(" " + bufferReceive[index]);
        }

        System.out.println(str);
        
        MPI.Finalize();
    }
}