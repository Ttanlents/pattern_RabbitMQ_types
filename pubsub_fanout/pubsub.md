###  Pub/Sub模式小结

在Pub/Sub模式下（Exchange类型为Fanout，也叫分列模式），消息首先被发送到Exchange，由Exchange路由到绑定的Queues中，类似于我们微信群，有什么事在群里面发送一下，群里面的人都能看得到；这样就不需要每个人单独发送消息了；



> **需要注意的两点：**
>
> **1、work、simple也会有交换机，他们使用的是默认的交换机**
>
> **2、Exchange还可以绑定到另一个Exchange上**