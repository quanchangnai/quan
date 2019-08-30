using System;
using System.Collections.Generic;
using System.Reflection;

namespace Quan.Message
{
    public class MessageFactory
    {
        protected readonly Dictionary<int, MessageBase> Prototypes = new Dictionary<int, MessageBase>();

        public void Register(MessageBase message)
        {
            if (message == null)
            {
                throw new NullReferenceException("参数[message]不能为空");
            }

            if (Prototypes.ContainsKey(message.Id))
            {
                throw new ArgumentException($"消息ID[{message.Id}]不能重复");
            }

            Prototypes.Add(message.Id, message);
        }

        public void Register(string assemblyName)
        {
            Assembly assembly;
            try
            {
                assembly = Assembly.Load(assemblyName);
            }
            catch (Exception e)
            {
                Console.WriteLine("加载程序集[{0}]出错", assemblyName);
                Console.WriteLine(e);
                return;
            }

            foreach (var type in assembly.GetTypes())
            {
                if (!typeof(MessageBase).IsAssignableFrom(type))
                {
                    continue;
                }

                try
                {
                    var message = Activator.CreateInstance(type) as MessageBase;
                    Register(message);
                }
                catch (Exception e)
                {
                    Console.WriteLine(e);
                }
            }
        }

        public MessageBase Create(int msgId)
        {
            Prototypes.TryGetValue(msgId, out var message);
            return message?.Create();
        }
    }
}