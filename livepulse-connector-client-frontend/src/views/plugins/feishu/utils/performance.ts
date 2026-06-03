/**
 * 性能优化工具函数
 */

/**
 * 防抖函数
 * 在事件被触发n秒后再执行回调，如果在这n秒内又被触发，则重新计时
 * @param fn 要执行的函数
 * @param delay 延迟时间（毫秒）
 * @returns 防抖后的函数
 */
export function debounce<T extends (...args: any[]) => any>(
  fn: T,
  delay: number = 300
): (...args: Parameters<T>) => void {
  let timer: NodeJS.Timeout | null = null;

  return function (this: any, ...args: Parameters<T>) {
    if (timer) {
      clearTimeout(timer);
    }

    timer = setTimeout(() => {
      fn.apply(this, args);
      timer = null;
    }, delay);
  };
}

/**
 * 节流函数
 * 规定在一个单位时间内，只能触发一次函数。如果这个单位时间内触发多次函数，只有一次生效
 * @param fn 要执行的函数
 * @param delay 延迟时间（毫秒）
 * @returns 节流后的函数
 */
export function throttle<T extends (...args: any[]) => any>(
  fn: T,
  delay: number = 300
): (...args: Parameters<T>) => void {
  let timer: NodeJS.Timeout | null = null;
  let lastTime = 0;

  return function (this: any, ...args: Parameters<T>) {
    const now = Date.now();
    const remaining = delay - (now - lastTime);

    if (remaining <= 0) {
      if (timer) {
        clearTimeout(timer);
        timer = null;
      }
      lastTime = now;
      fn.apply(this, args);
    } else if (!timer) {
      timer = setTimeout(() => {
        lastTime = Date.now();
        timer = null;
        fn.apply(this, args);
      }, remaining);
    }
  };
}

/**
 * 请求缓存类
 */
export class RequestCache<T = any> {
  private cache: Map<string, { data: T; timestamp: number }>;
  private ttl: number; // 缓存过期时间（毫秒）

  constructor(ttl: number = 5 * 60 * 1000) {
    // 默认 5 分钟过期
    this.cache = new Map();
    this.ttl = ttl;
  }

  /**
   * 获取缓存
   */
  get(key: string): T | null {
    const item = this.cache.get(key);

    if (!item) {
      return null;
    }

    // 检查是否过期
    if (Date.now() - item.timestamp > this.ttl) {
      this.cache.delete(key);
      return null;
    }

    return item.data;
  }

  /**
   * 设置缓存
   */
  set(key: string, data: T): void {
    this.cache.set(key, {
      data,
      timestamp: Date.now(),
    });
  }

  /**
   * 删除缓存
   */
  delete(key: string): void {
    this.cache.delete(key);
  }

  /**
   * 清空所有缓存
   */
  clear(): void {
    this.cache.clear();
  }

  /**
   * 清理过期缓存
   */
  cleanup(): void {
    const now = Date.now();
    for (const [key, item] of this.cache.entries()) {
      if (now - item.timestamp > this.ttl) {
        this.cache.delete(key);
      }
    }
  }
}

/**
 * 创建带缓存的异步函数
 * @param fn 异步函数
 * @param cache 缓存实例
 * @param keyGenerator 缓存键生成函数
 * @returns 带缓存的异步函数
 */
export function withCache<T extends (...args: any[]) => Promise<any>>(
  fn: T,
  cache: RequestCache<Awaited<ReturnType<T>>>,
  keyGenerator?: (...args: Parameters<T>) => string
): T {
  return (async function (this: any, ...args: Parameters<T>) {
    const key = keyGenerator
      ? keyGenerator(...args)
      : JSON.stringify(args);

    // 尝试从缓存获取
    const cached = cache.get(key);
    if (cached !== null) {
      return cached;
    }

    // 调用原函数
    const result = await fn.apply(this, args);

    // 存入缓存
    cache.set(key, result);

    return result;
  }) as T;
}

/**
 * 批量执行函数（分批处理大量数据）
 * @param items 数据项
 * @param fn 处理函数
 * @param batchSize 每批处理数量
 * @param delay 批次间延迟（毫秒）
 */
export async function batchProcess<T, R>(
  items: T[],
  fn: (item: T, index: number) => R,
  batchSize: number = 100,
  delay: number = 0
): Promise<R[]> {
  const results: R[] = [];

  for (let i = 0; i < items.length; i += batchSize) {
    const batch = items.slice(i, i + batchSize);
    const batchResults = await Promise.all(
      batch.map((item, index) => fn(item, i + index))
    );

    results.push(...batchResults);

    // 如果不是最后一批，且设置了延迟，则等待
    if (i + batchSize < items.length && delay > 0) {
      await new Promise(resolve => setTimeout(resolve, delay));
    }
  }

  return results;
}

/**
 * 延迟执行
 * @param ms 延迟时间（毫秒）
 */
export function delay(ms: number): Promise<void> {
  return new Promise(resolve => setTimeout(resolve, ms));
}

/**
 * 空闲时执行（requestIdleCallback）
 * @param fn 要执行的函数
 * @param timeout 超时时间（毫秒）
 */
export function runWhenIdle(
  fn: () => void,
  timeout: number = 2000
): void {
  if (typeof window !== 'undefined' && 'requestIdleCallback' in window) {
    (window as any).requestIdleCallback(() => fn(), { timeout });
  } else {
    setTimeout(fn, 0);
  }
}

/**
 * 取消令牌（用于取消异步操作）
 */
export class CancellationToken {
  private cancelled = false;

  cancel() {
    this.cancelled = true;
  }

  isCancelled(): boolean {
    return this.cancelled;
  }

  throwIfCancelled() {
    if (this.cancelled) {
      throw new Error('Operation cancelled');
    }
  }
}

/**
 * 创建带取消令牌的异步函数
 */
export async function withCancellable<T>(
  token: CancellationToken,
  fn: () => Promise<T>
): Promise<T> {
  token.throwIfCancelled();
  return await fn();
}

/**
 * 浅比较两个对象是否相等
 */
export function shallowEqual(obj1: any, obj2: any): boolean {
  if (obj1 === obj2) return true;

  if (typeof obj1 !== 'object' || obj1 === null || typeof obj2 !== 'object' || obj2 === null) {
    return false;
  }

  const keys1 = Object.keys(obj1);
  const keys2 = Object.keys(obj2);

  if (keys1.length !== keys2.length) return false;

  for (const key of keys1) {
    if (!Object.prototype.hasOwnProperty.call(obj2, key) || obj1[key] !== obj2[key]) {
      return false;
    }
  }

  return true;
}

/**
 * 深度比较两个对象是否相等（仅用于简单对象）
 */
export function deepEqual(obj1: any, obj2: any): boolean {
  if (obj1 === obj2) return true;

  if (typeof obj1 !== 'object' || obj1 === null || typeof obj2 !== 'object' || obj2 === null) {
    return false;
  }

  const keys1 = Object.keys(obj1);
  const keys2 = Object.keys(obj2);

  if (keys1.length !== keys2.length) return false;

  for (const key of keys1) {
    if (!Object.prototype.hasOwnProperty.call(obj2, key)) return false;
    if (!deepEqual(obj1[key], obj2[key])) return false;
  }

  return true;
}

/**
 * 格式化文件大小
 */
export function formatFileSize(bytes: number): string {
  if (bytes === 0) return '0 B';

  const k = 1024;
  const sizes = ['B', 'KB', 'MB', 'GB', 'TB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));

  return `${(bytes / Math.pow(k, i)).toFixed(2)} ${sizes[i]}`;
}

/**
 * 格式化数字（添加千位分隔符）
 */
export function formatNumber(num: number): string {
  return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
}

/**
 * 格式化持续时间
 */
export function formatDuration(ms: number): string {
  const seconds = Math.floor(ms / 1000);
  const minutes = Math.floor(seconds / 60);
  const hours = Math.floor(minutes / 60);
  const days = Math.floor(hours / 24);

  if (days > 0) {
    return `${days}天${hours % 24}小时`;
  } else if (hours > 0) {
    return `${hours}小时${minutes % 60}分钟`;
  } else if (minutes > 0) {
    return `${minutes}分钟${seconds % 60}秒`;
  } else {
    return `${seconds}秒`;
  }
}

/**
 * 节流执行（用于滚动事件优化）
 */
export function throttleScroll(callback: () => void, delay: number = 100) {
  let ticking = false;

  return function () {
    if (!ticking) {
      window.requestAnimationFrame(() => {
        callback();
        ticking = false;
      });
      ticking = true;
    }
  };
}

/**
 * 检测元素是否在视口中
 */
export function isInViewport(element: Element): boolean {
  const rect = element.getBoundingClientRect();
  return (
    rect.top >= 0 &&
    rect.left >= 0 &&
    rect.bottom <= (window.innerHeight || document.documentElement.clientHeight) &&
    rect.right <= (window.innerWidth || document.documentElement.clientWidth)
  );
}

/**
 * 检测元素是否部分在视口中
 */
export function isPartiallyInViewport(element: Element): boolean {
  const rect = element.getBoundingClientRect();
  return (
    rect.top < (window.innerHeight || document.documentElement.clientHeight) &&
    rect.bottom > 0 &&
    rect.left < (window.innerWidth || document.documentElement.clientWidth) &&
    rect.right > 0
  );
}
