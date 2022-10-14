// 时间格式化
export default function formatLastDate(lastDate){
  if(!lastDate) return;
  // 当前时间
  let date = new Date();
  // 最后一条信息时间
  let date1 = new Date(lastDate);
  // 判断是否同年
  if(date.getFullYear() != date1.getFullYear()){
    return lastDate.split(" ");// 返回年月日
  }
  // 判断是否为四天以前
  if(date.getMonth() !== date1.getMonth() || 
    (date.getDate() - date1.getDate() > 3)
  ){
    lastDate = lastDate.split(" ")[0];
    lastDate = lastDate.substring(lastDate.indexOf("-") + 1);
    return lastDate;
  }
  // 判断是否为1-3天前
  if(date.getDate() - date1.getDate() > 1){
    return date.getDate() - date1.getDate() + "天前";
  }
  // 判断是否为昨天
  if(date.getDate() - date1.getDate() === 1) {
    return "昨天";
  }
  // 当天，显示信息
  lastDate = lastDate.split(" ")[1];
  lastDate = lastDate.substring(0, lastDate.lastIndexOf(":"));
  return lastDate;
}