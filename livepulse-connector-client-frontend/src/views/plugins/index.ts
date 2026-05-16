import { markRaw } from "vue";
import type { RouteComponent } from "vue-router";
import Wecom from "./wecom/index.vue"; //企微
import Shopify from "./shopify/index.vue"; //Shopify
import Shopee from "./shopee/index.vue"; //Shopee
import Ga4 from "./ga4/index.vue"; //GA4
import Tiktok from "./tiktok/index.vue"; //TikTok
import Tiktokshop from "./tiktokshop/index.vue"; //TikTok Shop
import WechatOfficialAccount from "./wechat-official-account/index.vue"; //微信服务号
import Facebook from "./facebook/index.vue"; //Facebook
import Salesforce from "./salesforce/index.vue"; //Salesforce
import Youzan from "./youzan/index.vue"; //有赞商城
import Baidu from "./baidu/index.vue"; //百度营销
import GoogleAds from "./google-ads/index.vue"; //Google Ads

interface PluginItem {
  name: string;
  title: string;
  aliases?: string[]; // 可能的别名
  component: RouteComponent;
}

export const pluginConfig: Array<PluginItem> = [
  {
    name: "wecom",
    title: "企业微信插件",
    aliases: ["企微", "企业微信", "企业微信连接器", "wecom", "WeCom"],
    component: markRaw(Wecom)
  },
  {
    name: "shopify",
    title: "Shopify插件",
    aliases: ["shopify", "Shopify", "SHOPIFY"],
    component: markRaw(Shopify)
  },
  {
    name: "shopee",
    title: "Shopee插件",
    aliases: ["shopee", "Shopee", "SHOPEE", "虾皮"],
    component: markRaw(Shopee)
  },
  {
    name: "ga4",
    title: "Google Analytics 4 连接器",
    aliases: ["ga4", "GA4", "Google Analytics", "Google Analytics 4", "GA4连接器"],
    component: markRaw(Ga4)
  },
  {
    name: "tiktok",
    title: "TikTok 连接器",
    aliases: ["tiktok", "TikTok", "TIKTOK", "抖音", "TikTok连接器"],
    component: markRaw(Tiktok)
  },
  {
    name: "tiktokshop",
    title: "TikTok Shop 连接器",
    aliases: ["tiktokshop", "TikTok Shop", "TikTokShop", "TIKTOK SHOP", "抖音小店", "TikTok Shop连接器"],
    component: markRaw(Tiktokshop)
  },
  {
    name: "wechat-official-account",
    title: "微信服务号连接器",
    aliases: ["wechat-official-account", "微信服务号", "微信公众号", "微信服务号连接器", "WeChat Official Account"],
    component: markRaw(WechatOfficialAccount)
  },
  {
    name: "facebook",
    title: "Facebook连接器",
    aliases: ["facebook", "Facebook", "FACEBOOK", "FB", "Facebook连接器"],
    component: markRaw(Facebook)
  },
  {
    name: "salesforce",
    title: "Salesforce连接器",
    aliases: ["salesforce", "Salesforce", "SALESFORCE", "Salesforce连接器", "SFDC"],
    component: markRaw(Salesforce)
  },
  {
    name: "youzan",
    title: "有赞商城连接器",
    aliases: ["youzan", "Youzan", "YOUZAN", "有赞", "有赞商城", "有赞连接器", "有赞商城连接器"],
    component: markRaw(Youzan)
  },
  {
    name: "baidu",
    title: "百度营销连接器",
    aliases: ["baidu", "Baidu", "BAIDU", "百度", "百度营销", "百度连接器", "百度营销连接器"],
    component: markRaw(Baidu)
  },
  {
    name: "google-ads",
    title: "Google Ads连接器",
    aliases: ["google-ads", "Google Ads", "GOOGLE ADS", "Google Ads连接器", "谷歌广告", "Google广告"],
    component: markRaw(GoogleAds)
  }
];
