# android.os.TransactionTooLargeException

>注意：在 Android 7.0（API 级别 24）或更高版本中，系统会在运行时抛出 TransactionTooLargeException 异常。在较低版本的 Android 中，系统仅在 logcat 中显示警告。 

[TransactionTooLargeException](https://developer.android.google.cn/reference/android/os/TransactionTooLargeException.html)

  Binder事务失败，因为它太大。
  
  在远程过程调用期间，调用的参数和返回值将作为存储在Binder事务缓冲区中的Bundle对象进行传输。如果参数或返回值太大而无法容纳在事务缓冲区中，则调用将失败，并且将引发TransactionTooLargeException。
  
  Binder事务缓冲区具有有限的固定大小，当前为1Mb，该进程的所有正在进行的事务共享该大小。因此，即使有许多交易正在进行，即使有许多交易正在进行，也可能引发此异常。
  
  远程过程调用引发TransactionTooLargeException时，有两种可能的结果。
  
  客户端无法将其请求发送到服务（最有可能的情况是，参数太大而无法容纳在事务缓冲区中），
  
  或者服务无法将其响应发送回客户端（最有可能的情况是，返回值是太大而无法容纳事务缓冲区）。无法确定这些结果中的哪一个实际发生。客户端应假定发生了部分故障。
  
  避免TransactionTooLargeException的关键是使所有事务保持相对较小。
  
  尝试最小化为参数和远程过程调用的返回值创建Parcel所需的内存量。避免传输大量字符串或大位图。如果可能，请尝试将大请求分解为较小的部分。
  
  如果要实现服务，则可能会在客户端可以执行的查询上强加大小或复杂性约束。
  
  例如，如果结果集可能变大，则不允许客户端一次请求多个记录。或者，不是一次返回所有可用数据，而是先返回基本信息，然后让客户根据需要稍后再提供其他信息

 [Parcelable 和 Bundle](https://developer.android.google.cn/guide/components/activities/parcelables-and-bundles?hl=zh_cn)

> onSaveInstance 把数据存放在Binder事务缓冲区中。目前Binder事务缓冲区为1MB，由进程中正在处理的所有事务共享。
>
>对于 savedInstanceState 的具体情况，应将数据量保持在较小的规模，因为只要用户可以返回到该 Activity，系统进程就需要保留所提供的数据（即使 Activity 的进程已终止）。
>我们建议您将保存的状态保持在 50k 数据以下。 
>
>Binder 事务缓冲区的大小固定有限，目前为 1MB，由进程中正在处理的所有事务共享。
>由于此限制是进程级别而不是 Activity 级别的限制，因此这些事务包括应用中的所有 binder 事务，
>例如 onSaveInstanceState，startActivity 以及与系统的任何互动。超过大小限制时，将引发 TransactionTooLargeException。 