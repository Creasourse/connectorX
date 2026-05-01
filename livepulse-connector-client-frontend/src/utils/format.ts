import dayjs from "dayjs";
import relativeTime from "dayjs/plugin/relativeTime";
import "dayjs/locale/zh-cn";
dayjs.extend(relativeTime);

// expose dayjs for template usage
const formatDate = (date: string, fmt = "YYYY-MM-DD") =>
  dayjs(date).format(fmt);
const fromNow = (date: string) => dayjs(date).locale("zh-cn").fromNow();
const formatNow = (fmt = "YYYY-MM-DD HH:mm:ss") =>
  dayjs().locale("zh-cn").format(fmt);

const getUrl = v => {
  const url = v.replace("/data/connectorX/uploadPath/", "");
  return `http://connectorx.livepulse.com.cn/images/${url}`;
};

export { formatDate, fromNow, formatNow, getUrl };
