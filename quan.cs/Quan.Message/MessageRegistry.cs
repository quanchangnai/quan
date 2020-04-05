using System;
using System.Collections.Generic;
using System.Reflection;

namespace Quan.Message
{
    /// <summary>
    /// 消息注册表
    /// </summary>
    public class MessageRegistry
    {
        private readonly Dictionary<int, MessageBase> _messages = new Dictionary<int, MessageBase>();

        public MessageRegistry()
        {
        }

        /// <summary>
        /// 通过反射自动注册消息
        /// </summary>
        /// <param name="messageAssembly">消息所在的程序集名字</param>
        public MessageRegistry(string messageAssembly)
        {
            Register(messageAssembly);
        }

        public void Register(MessageBase message)
        {
            if (message == null)
            {
                throw new NullReferenceException("参数[message]不能为空");
            }

            if (_messages.ContainsKey(message.Id))
            {
                throw new ArgumentException($"消息ID[{message.Id}]不能重复");
            }

            _messages.Add(message.Id, message);
        }

        public void Register(string messageAssembly)
        {
            Assembly assembly;
            try
            {
                assembly = Assembly.Load(messageAssembly);
            }
            catch (Exception e)
            {
                Console.WriteLine("加载程序集[{0}]出错", messageAssembly);
                Console.WriteLine(e);
                return;
            }

            foreach (var type in assembly.GetTypes())
            {
                if (!typeof(MessageBase).IsAssignableFrom(type) || type.IsAbstract)
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
            _messages.TryGetValue(msgId, out var message);
            return message?.Create();
        }
    }
}