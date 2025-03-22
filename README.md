# 后端
## pom 坐标
```xml
<dependency>
    <groupId>com.nmgjc</groupId>
    <artifactId>jc-sensitive-word-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

## yml 配置项
```yaml
# 敏感词过滤
sensitive-word:
  # TrWebOCR的api路径
  ocrUrl: http://localhost:8089/api/tr-run/
  # 敏感词校验路径
  urlPatterns:
    - /xxx/xxxx
    - /xxxx/**
```

## 安装 jar 包
:::info
没上传仓库，先手动安装一下吧

将`xxx`改成 jar 包存放目录。

:::

```shell
mvn install:install-file  -Dfile=xxx/jc-sensitive-word-boot-starter-1.0.0.jar  -DgroupId=com.nmgjc  -DartifactId=jc-sensitive-word-boot-starter  -Dversion=1.0.0  -Dpackaging=jar
```

# 前端
## 效果
![](https://cdn.nlark.com/yuque/0/2025/png/32587996/1742269148137-b8a6ab48-f25a-4934-930d-0f6e98dc968c.png)

![](https://cdn.nlark.com/yuque/0/2025/png/32587996/1742269241577-6a6c5cdd-1f84-4aea-ab5b-35b91490c724.png)

## 响应体
> [!NOTE]
> 
>`fileUpload：`是否文件上传的请求。如果是为 true，代表文件上传的请求。文件上传有专门的组件，文件上传想检测敏感词，就用那个组件。
> 
>`triggerType：`敏感词类型，1 为禁止性，2 为提示性。
> 
>`words：`监测到的敏感词列表，默认返回三个。


```json
{
    "msg": "您所提交的信息包含禁止性敏感词，请修改后重新发布！",
    "code": 602,
    "data": {
        "fileUpload": true,
        "triggerType": "1",
        "words": [
            "bao炸"
        ]
    }
}
```

## axios 响应拦截器
> [!NOTE]
>
>后端监测到敏感词后响应码是 602，只需把下面代码添加到 axios 的响应拦截器中即可。
>并不是必须用下面这段代码，可以根据实项目情况完全可以自定义。


>[!WARNING]
>
>但是提示性的敏感词，点击`继续`时 header 中必须传`X-Ignore-Sensitive-Check`，而且值是`true`。


```javascript
} else if(code === 602) {
      const { triggerType, words, fileUpload} = res.data.data
      if(!fileUpload) {
        if (triggerType === "1") {
          const m = `<span style="font-weight: bold; font-size: ">${msg} <br> 禁止性敏感词：<span style="color: #ff4d4f; font-weight: bold">${words}</span>等。</span>`
          return ElMessageBox.alert(
              m,
              "提示",
              {
                confirmButtonText: '关闭',
                dangerouslyUseHTMLString: true,
                type: 'error',
                showClose: false,
              }
          ).then(() => {
            return Promise.reject(new Error(msg))
          })
        } else if (triggerType === "2") {
          const m = `<span style="font-weight: bold; font-size: ">${msg} <br> 提示性敏感词：<span style="color: #faad14; font-weight: bold">${words}</span>等。</span>`
          return ElMessageBox.confirm(m, '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            dangerouslyUseHTMLString: true,
            type: 'warning',
            showClose: false,
          }).then(() => {
            const originalRequest = res.config;
            originalRequest.headers['X-Ignore-Sensitive-Check'] = 'true';
            return service(originalRequest)
          }).catch(() => {
            return Promise.resolve(res.data)
          })
        }
      } else {
        return  Promise.resolve(res.data)
      }
    }
```

![](https://cdn.nlark.com/yuque/0/2025/png/32587996/1742268520010-cef17e2b-47c5-415b-aeea-020c093e7f7d.png)

## 文件上传组件
> [!NOTE]
>
>根据项目实际情况自己调整。


```vue
<template>
  <div class="app-container">
    <el-upload :key="key"
      multiple
      :auto-upload="false"
      :file-list="fileList"
      :limit="limit"
      :on-exceed="handleExceed"
      :on-change="handleChange"
      ref="fileUpload"
      class="upload-file-uploader"
      drag
    >
      <el-icon class="el-icon--upload"><upload-filled /></el-icon>
      <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
    </el-upload>
  </div>
</template>

<script setup>
import { ref, defineExpose } from "vue";
import { getToken } from "@/utils/auth";
import {UploadFilled} from "@element-plus/icons-vue";
import request from '@/utils/request'
import {ElMessageBox} from "element-plus";

const props = defineProps({
  modelValue: [String, Object, Array],
  // 数量限制
  limit: {
    type: Number,
    default: 5,
    validator: (v) => v > 0
  },
  // 文件类型, 例如['png', 'jpg', 'jpeg']
  fileType: {
    type: Array,
    default: () => ["doc", "xls", "txt", "pdf", "docx", "xlsx", "png", "jpg", "jpeg"],
  },
  uploadFileUrl: {
    type: String,
    default: undefined
  },
  // 上传成功回调
  callback: {
    type: Function,
    default: () => {}
  }
});

const key = ref("1")

const { proxy } = getCurrentInstance();
const uploadFileUrl = ref(props.uploadFileUrl || "/common/upload"); // 上传文件服务器地址
const headers = ref({ Authorization: "Bearer " + getToken(), 'Content-Type': 'multipart/form-data' });
const fileList = ref([]);

const MIME_SIZE_LIMITS = {
  image: 2,   // MB
  default: 10  // MB
};

// 优化文件扩展名校验
function getFileExtension(filename) {
  return filename.split('.').pop()?.toLowerCase() || '';
}

function handleBeforeUpload(file) {
  // 简化文件扩展名获取逻辑
  const fileExt = getFileExtension(file.name);
  if (props.fileType.length && !props.fileType.includes(fileExt)) {
    proxy.$modal.msgError(`请上传 ${props.fileType.join("/")} 格式文件`);
    return false;
  }

  // 合并文件名校验逻辑
  if (/[,\\]/.test(file.name)) { // 增加更多特殊字符校验
    proxy.$modal.msgError('文件名包含非法字符（,、\\）');
    return false;
  }

  // 优化大小校验逻辑
  const maxSize = file.raw.type.startsWith('image/')
      ? MIME_SIZE_LIMITS.image
      : MIME_SIZE_LIMITS.default;

  if (file.size / 1024 / 1024 > maxSize) {
    proxy.$modal.msgError(`${file.name} 超过${maxSize}MB限制`);
    return false;
  }
  return true;
}

// 文件个数超出
function handleExceed() {
  proxy.$modal.msgError(`上传文件数量不能超过 ${props.limit} 个!`);
}



function handleChange(file, fList) {
  if(!handleBeforeUpload(file)) {
    // 使用更可靠的清除方法
    const uploadRef = proxy.$refs.fileUpload;
    uploadRef.handleRemove(file);
    return false;
  }
  fileList.value = fList;
}


async function submitFileForm() {
  if(!fileList.value.length) {
    proxy.$modal.msgError("请先选择文件");
    return false;
  }

  // 构建 FormData 增加错误处理
  const formData = new FormData();
  try {
    fileList.value.forEach(file => {
      if (!file.raw) throw new Error("invalid_file");
      formData.append("files", file.raw);
    });
  } catch (e) {
    proxy.$modal.msgError("包含无效文件，请重新选择");
    return false;
  }

  // 公共配置
  const requestConfig = {
    timeout: 30000,
    headers: headers.value
  };

  try {
    proxy.$modal.loading("正在上传文件...");
    const response = await request.post(uploadFileUrl.value, formData, requestConfig);

    const { code, msg, data } = response;
    const { triggerType, words } = data || {};

    // 敏感词处理
    if (code === 602) {
      proxy.$modal.closeLoading();
      await handleSensitiveCheck({ triggerType, msg, words, formData });
      return;
    }

    // 成功处理
    proxy.$modal.msgSuccess("文件上传成功");
    props.callback();
  } catch (error) {
    proxy.$modal.msgError("文件上传失败");
    console.error('Upload error:', error);
  } finally {
    proxy.$modal.closeLoading();
    clearFileList();
  }
}

// 敏感词处理逻辑
async function handleSensitiveCheck({ triggerType, msg, words, formData }) {
  const messageTypeConfig = {
    '1': {
      type: 'error',
      title: '禁止上传',
      actionText: '关闭',
      handler: null
    },
    '2': {
      type: 'warning',
      title: '提示',
      actionText: '确定',
      handler: async () => {
        try {
          proxy.$modal.loading("正在重新上传文件...");

          const requestConfig = {
            timeout: 30000,
            headers: {
              Authorization: "Bearer " + getToken(),
              'Content-Type': 'multipart/form-data',
              'X-Ignore-Sensitive-Check': 'true'
            },
          };

          const response = await request.post(uploadFileUrl.value, formData, requestConfig);

          if (response.code === 200) {
            proxy.$modal.msgSuccess("文件上传成功");
            props.callback();
          }
        } finally {
          proxy.$modal.closeLoading();
        }
      }
    }
  };

  const config = messageTypeConfig[triggerType];
  if (!config) return;

  const messageContent = generateMessageHTML(msg, words, triggerType);

  if (triggerType === '1') {
    await ElMessageBox.alert(messageContent, config.title, {
      dangerouslyUseHTMLString: true,
      confirmButtonText: config.actionText,
      type: config.type,
      showClose: false
    });
    return;
  }

  try {
    await ElMessageBox.confirm(messageContent, config.title, {
      dangerouslyUseHTMLString: true,
      confirmButtonText: config.actionText,
      cancelButtonText: '取消',
      type: config.type,
      showClose: false
    });
    await config.handler?.();
  } catch (error) {
    console.log('error', error)
    if (error !== 'cancel') {
      proxy.$modal.msgError("操作失败");
    }
  }
}

// 生成消息 HTML 模板
function generateMessageHTML(msg, words, type) {
  const categoryMap = {
    '1': { name: '禁止性', color: '#ff4d4f' },
    '2': { name: '提示性', color: '#faad14' }
  };
  const { name, color } = categoryMap[type] || {};

  return `
    <div class="sensitive-alert">
      <div class="alert-title">${msg}</div>
      <div class="alert-content">
        检测到${name}敏感词：
        <span style="color: ${color}; font-weight: 500">${words}</span> 等
      </div>
    </div>
  `;
}



// 清理文件列表
function clearFileList() {
  proxy.$refs.fileUpload?.clearFiles();
  fileList.value = [];
}

defineExpose(({
  submitFileForm
}))
</script>
<style scoped lang="scss">
.upload-file-uploader {
  margin-bottom: 5px;
}
.upload-file-list .el-upload-list__item {
  border: 1px solid #e4e7ed;
  line-height: 2;
  margin-bottom: 10px;
  position: relative;
}
.upload-file-list .ele-upload-list__item-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: inherit;
}
.ele-upload-list__item-content-action .el-link {
  margin-right: 10px;
}
.sensitive-alert {
  line-height: 1.6;
}

.alert-title {
  font-weight: 500;
  margin-bottom: 8px;
}

.alert-content {
  color: rgba(0, 0, 0, 0.85);
}
</style>


```

# 使用方式
> [!NOTE]
>
>提供了两种发式：
>1. 基于拦截器
>2. 基于切面


## 拦截器
> [!NOTE]
>
>默认项目启动会注册拦截器。
>但是如果配置文件中的`sensitive-word.urlPatterns`未配置任何路径就不会注册拦截器。


## 切面
> [!NOTE]
>
>在 Controller 上添加注解`@SensitiveWord`即可。


## 三方调用接口
### 普通 JOSN 串
> [!NOTE]
>
>POST xxx/sensitiveWordCheck/sensitive


**Body 请求参数**

```json
{
  "content": "bao炸"
}
```

**返回示例**

```json
{
  "msg": "操作成功",
  "code": 200,
  "data": [
    {
      "word": "bao炸",
      "tags": [
        "1"
      ]
    }
  ]
}
```

```json
{
  "msg": "错误信息",
  "code": 500
}
```

**返回数据结构**

| 名称 | 类型 | 说明 |
| --- | --- | --- |
| » msg | string |  |
| » code | integer |  |
| » data | [object] |  |
| »» word | string | 敏感词 |
| »» tags | [string] | 敏感词类型：1为禁止性，2为提示性 |


### 文件类型检测
> [!NOTE]
>
>POST xxx/sensitiveWordCheck/file


```java
@PostMapping("/file")
public HttpResult checkFile(@RequestParam("files") MultipartFile[] files){}
```

**返回示例**

跟上面一样。

