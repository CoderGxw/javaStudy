# java学习笔记  
> <<java多线程编程实战指南-第二版>>

## 基础理论
- Java中的线程分为<b>守护线程</b>（Daemon Thread）和<b>用户线程</b>(User Thread)， 其中用户线程会影响Java虚拟机的正常停止，即java虚拟机停止前所有的用户线程都必须停止。守护线程不影响Java虚拟机的正常停止。
- 线程的属性通常由父线程决定，父线程是守护线程则子线程也是，用户线程同理。也可以通过setDaemon方法来修改。

## 线程的生命周期
![线程生命周期](img/img.png)
- <b>NEW</b>:创建状态，线程刚创建未启动时的状态。一个线程对象只能创建一次，因此一个线程只可能处于该状态一次。
- <b>RUNNABLE</b>:运行状态，该状态包含两个子状态READY和RUNNING，线程被调度时，为RUNNING状态，即线程中的run()中的代码正在CPU中运行，当Thread实例的yield()被调用时，处于READY状态。
- <b>BLOCKED</b>:阻塞状态，当线程被IO阻塞或者被其他线程的锁阻塞时，处于此状态，当阻塞结束后，返回RUNNABLE状态。
- <b>WAITING</b>:无限等待状态，处于等待其他线程执行特定操作的状态。
- <b>TIMED_WAITING</b>:有限等待状态，与WAITING的区别在于，此状态有时间限制，当其他线程没有在指定时间内完成操作时，该线程自动切换为RUNNABLE。
- <b>TERMINATED</b>:结束状态，一个线程只能处于该状态一次。当run() 方法正常返回或异常终止之后，都处于此状态。

注：从RUNNABLE切换到BLOCKED、WAITING、TIMED_WAITING都意味着上下文（Context）切换。

## 原子性、内存可见性、重排序
- **概念**：
     - **原子性**：对于涉及共享变量的操作，对外部线程来看是密不可分的，则为原子操作。 i++不是原子操作，该操作实际可以分为三个子操作：将变量i加载到寄存器中，将寄存器中的值+1，将寄存器中的值写回变量。
     - **内存可见行**：CPU在执行代码的时候，为了降低变量访问的时间开销，可能将代码中访问的变量值缓存到该CPU的高速缓存（如L1 Cache、L2 Cache等）中。因此当相应代码再次访问某个变量时，相应的值可能是从CPU的高速缓存而不是主内存中读取的。同样地，出于对内存访问效率的考虑，代码对变量值的修改也可能仅被写入执行这段代码的CPU上的写缓冲器（ Store Buffer）里，而没有被写入该PU的高速缓存里，更没有被写入主内存里。由于每个CPU都有自己的高速缓存，而一个CPU并不能直接读取其他CPU上的高速缓存里的内容，这就导致一个线程对共享变量所做的更新可能无法被其他CPU上运行的其他线程“看到”。这就是所谓的内存可见性。
- **synchronized**关键字：该关键字可以实现操作的原子性和内存可见性。
   - 原理：该关键字所限定的临界区具有排他性，能保证在任一时刻只有一个线程可以执行该临界区中的代码。从而保证了原子性。该关键字还有另一个作用就是保证了一个线程对共享变量的修改对稍后执行该临界区中的代码的线程来说是可见的。
- **volatile**关键字：该关键字也能够保存内存可见性。即一个线程对采用了volatile修饰的变量的值的修改，对其他线程内存可见。
   - 原理：volatile关键字保障内存可见性的核心机制是，当一个线程修改了一个 volatile关键字修饰的变量的值时，该值会被写入当前线程所在的CPU上的高速缓存里，而不是仅仅停留在该CPU的写缓冲器里，而其他CPU上的高速缓存里存储的该变量的值（副本）也会因此而失效。这就保证了这些其他线程再访问该 volatile关键字修饰的变量时总是可以通过处理器的缓存一致性协议（ Coherence Protocol）来获取该变量的最新值。
   - 另外的作用：禁止了重排序。
- 两个关键字对比：与 volatile相比， synchronized既能保证操作的原子性，又能保证内存可见性，而 volatile仅能保证内存可见性。但是synchronized会导致上下文切换，而volatile不会。

## Immutable Object(不可变对象)模式
- **概念**：使用对外可见的状态不可变的对象，使得被共享对象"天生"具有线程安全性而无须进行额外的同步访问控制。
- **不可变的对象**：即一经创建，其对外的状态就保持不变的对象，例如java中的Integer和String。
- **模式架构**：
  - **ImmutableObject**：负责存储不可变状态。该参与者不对外暴露任何可以修改其状态的方法。
      - 其中参数StateX和StateN通过构造器获取值。
      - getX方法返回该实例维护的状态的对应值。
      - StateSnapShot返回该实力维护的一组状态快照。
  - **Mainpulator**：维护ImmutableObject所描述的实体状态的变更。每当有状态变更时，负责生成新的ImmutableObject实例以反应状态的变更。
      - changeStateTo是根据新的状态值生成新的ImmutableObject实例。
  
![Immutable模式类图](img/img1.png)
-  一个严格意义上的**状态不可变对象**需要满足以下所有条件。
    - 1.类本身使用 final修饰:防止其子类改变其定义的行为。
    - 2.所有字段都是用 final修饰的:使用fial修饰不仅仅是从语义上说明被修饰字段的引用不可改变，更重要的是这个语义在多线程环境下由JMM（ Java Memory Model）保证了被修饰字段所引用对象的初始化安全，即 final修饰的字段在其他线程可见时，它必定已完成初始化。相反，非 final修饰的字段由于缺少这种保证，可能导致在一个线程“看到”一个字段的时候，它还未完成初始化，从而可能导致一些不可预料的结果。
    - 3.在创建对象的过程中没有泄露this关键字给其他类:防止其他类（如该类的内部匿名类）在对象创建过程中修改其状态。
    - 4.若任何字段引用了其他状态可变的对象（如集合、数组等），则这些字段必须是由 private关键字修饰的，并且这些字段的值不能对外暴露。若有相关方法要返回这些字段的值，应该进行防御性复制。


