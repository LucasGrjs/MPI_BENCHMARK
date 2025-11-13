# MPI_BENCHMARK

A comprehensive benchmarking suite for measuring the performance of various **Message Passing Interface (MPI) collective and point-to-point communication operations** implemented in **Java**.

---

## 🚀 Features

This repository provides benchmarks for key MPI operations, allowing users to measure latency and bandwidth across different implementations or hardware configurations.

The benchmarks include:

| Category | Operations |
| :--- | :--- |
| **Point-to-Point** | `ISend` (Non-blocking Send) |
| **Collective** | `AllGather`, `AllGatherV` |
| | `Gather`, `GatherV`, `GatherVByte` |
| | `Scatter` |
| | `AllToAll`, `AllToAllv`, `AllToAllvByte` |
| **Specialized** | `allToAllvGAMA`, `allToAllvGAMA2` (tailored for a specific memory architectures) |

---

## 🛠️ Prerequisites

To build and run these benchmarks, you need a system configured for parallel Java execution using MPI.

* **Java Development Kit (JDK):** Version 8 or higher.
* **MPI Implementation:** An MPI library, such as **MPICH** or **OpenMPI**.
* **Java MPI Wrapper:** An implementation like **MPJ Express** or a similar wrapper that allows Java applications to interact with the underlying MPI library.

---

```bash
# Example: Running the AllGather benchmark with 4 processes

mpirun -np 4 java AllGather
# OR (if using MPJ Express):
# mpjrun.sh -np 4 AllGather
```
