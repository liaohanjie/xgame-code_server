package com.game.gameServer.msg;


/**
 * 抽象的 CG 的消息
 * 
 * @author hjj2019
 * @since 2015/01/25
 *
 */
public abstract class AbstractCGMsgObj<THandler extends AbstractCGMsgHandler<?>> extends AbstractExecutableMsgObj {
	@Override
	public void exec() {
		@SuppressWarnings("unchecked")
		AbstractCGMsgHandler<AbstractCGMsgObj<?>> 
			handler = (AbstractCGMsgHandler<AbstractCGMsgObj<?>>)this.getSelfHandler();

		if (handler == null) {
			// 如果处理器为空, 
			// 则直接退出!
			return;
		}

		// 获取消息处理器
		handler.handle(this);
	}

	/**
	 * 获取消息处理器
	 * 
	 * @return
	 * 
	 */
	public abstract THandler getSelfHandler();
}