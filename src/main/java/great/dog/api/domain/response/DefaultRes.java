package great.dog.api.domain.response;

import great.dog.api.util.StatusCode;
import great.dog.api.util.StatusMsg;
import lombok.*;

@Data
@AllArgsConstructor
@Builder
@Getter @Setter
public class DefaultRes<T> {

	private int resCode;
	private String resMsg;
	private T data;

	public DefaultRes(final int resCode, final String resMsg) {
		this.resCode = resCode;
		this.resMsg = resMsg;
		this.data = null;
	}

	public DefaultRes() {
		this.resCode = StatusCode.OK;
		this.resMsg = StatusMsg.RES_SUCCESS;
		this.data = null;
	}

	public static<T> DefaultRes<T> res(final int resCode, final String resMsg) {
		return res(resCode, resMsg, null);
	}

	public static<T> DefaultRes<T> res(final int resCode, final String resMsg, final T t) {
		return DefaultRes.<T>builder()
				.data(t)
				.resCode(resCode)
				.resMsg(resMsg)
				.build();
	}

}