# 项目搜索 API 文档

## 概述

本文档介绍项目搜索功能的 API 接口使用方法，支持按项目状态、名称、ID 等多条件查询。

所有查询接口统一使用 `getProjects()` 方法，简洁高效。

---

## 1. 高级搜索接口（推荐）

### POST `/project/search`

支持多条件组合搜索，功能最强大。

#### 请求示例

**1.1 按状态搜索（核心功能）**

```json
POST /project/search
Content-Type: application/json

{
  "status": "进行中",
  "pageNum": 1,
  "pageSize": 10
}
```

**1.2 按名称模糊搜索**

```json
{
  "name": "电商",
  "pageNum": 1,
  "pageSize": 10
}
```

**1.3 组合搜索：状态 + 名称**

```json
{
  "status": "进行中",
  "name": "电商",
  "pageNum": 1,
  "pageSize": 10
}
```

**1.4 带排序的搜索**

```json
{
  "status": "进行中",
  "sortField": "create_time",
  "sortOrder": "desc",
  "pageNum": 1,
  "pageSize": 10
}
```

**1.5 按 ID 精确查询**

```json
{
  "id": 1
}
```

#### 请求参数说明

| 参数名 | 类型 | 必填 | 说明 | 示例 |
|--------|------|------|------|------|
| id | Integer | 否 | 项目 ID（精确查询） | 1 |
| name | String | 否 | 项目名称（模糊查询） | "电商" |
| status | String | 否 | 项目状态（精确查询） | "进行中" |
| pageNum | Integer | 否 | 页码，默认 1 | 1 |
| pageSize | Integer | 否 | 每页数量，默认 10 | 10 |
| sortField | String | 否 | 排序字段 | "create_time" |
| sortOrder | String | 否 | 排序方向（asc/desc），默认 desc | "desc" |

**sortField 可选值：**
- `id` - 按 ID 排序
- `name` - 按名称排序
- `status` - 按状态排序
- `create_time` - 按创建时间排序（默认）

**常见项目状态：**
- `进行中`
- `已完成`
- `已暂停`
- `计划中`

#### 响应示例

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "total": 25,
    "pages": 3,
    "current": 1,
    "size": 10,
    "records": [
      {
        "id": 1,
        "name": "电商平台项目",
        "status": "进行中",
        "cover": "https://example.com/cover1.jpg",
        "createTime": "2024-11-24 14:30:00"
      },
      {
        "id": 2,
        "name": "电商后台管理系统",
        "status": "进行中",
        "cover": "https://example.com/cover2.jpg",
        "createTime": "2024-11-23 10:15:00"
      }
    ]
  }
}
```

---

## 2. 按状态查询接口（快捷方式）

### GET `/project/status/{status}`

快捷的 GET 方式接口，专门用于按状态查询。

#### 请求示例

```bash
# 查询"进行中"的项目
GET /project/status/进行中?page=1&pageSize=10

# 查询"已完成"的项目
GET /project/status/已完成?page=1&pageSize=10

# 查询"已暂停"的项目
GET /project/status/已暂停
```

#### 请求参数说明

| 参数名 | 位置 | 类型 | 必填 | 说明 | 示例 |
|--------|------|------|------|------|------|
| status | Path | String | 是 | 项目状态 | "进行中" |
| page | Query | Integer | 否 | 页码，默认 1 | 1 |
| pageSize | Query | 否 | Integer | 每页数量，默认 10 | 10 |

#### 响应示例

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "total": 15,
    "pages": 2,
    "records": [
      {
        "id": 1,
        "name": "电商平台项目",
        "status": "进行中",
        "cover": "https://example.com/cover.jpg",
        "createTime": "2024-11-24 14:30:00"
      }
    ]
  }
}
```

---

## 3. 查询所有项目接口

### GET `/project`

分页查询所有项目，不带任何搜索条件。

#### 请求示例

```bash
GET /project?page=1&pageSize=10
```

#### 请求参数说明

| 参数名 | 类型 | 必填 | 说明 | 示例 |
|--------|------|------|------|------|
| page | Integer | 否 | 页码，默认 1 | 1 |
| pageSize | Integer | 否 | 每页数量，默认 10 | 10 |

---

## 使用场景

### 场景 1：项目管理页面 - 按状态筛选

**需求：** 在项目列表页面，用户点击状态标签筛选项目

**推荐接口：** `GET /project/status/{status}`

```javascript
// 前端示例代码
async function filterByStatus(status) {
  const response = await fetch(`/project/status/${status}?page=1&pageSize=10`);
  const data = await response.json();
  displayProjects(data.data.records);
}

// 用户点击"进行中"标签
filterByStatus('进行中');
```

### 场景 2：高级搜索页面 - 多条件组合

**需求：** 用户在搜索表单中输入多个条件

**推荐接口：** `POST /project/search`

```javascript
// 前端示例代码
async function advancedSearch(searchForm) {
  const response = await fetch('/project/search', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      name: searchForm.name,
      status: searchForm.status,
      sortField: 'create_time',
      sortOrder: 'desc',
      pageNum: 1,
      pageSize: 10
    })
  });
  const data = await response.json();
  displayProjects(data.data.records);
}
```

### 场景 3：项目状态统计

**需求：** 统计不同状态的项目数量

```javascript
// 查询各状态项目数量
async function getStatusStatistics() {
  const statuses = ['进行中', '已完成', '已暂停', '计划中'];
  const statistics = {};
  
  for (const status of statuses) {
    const response = await fetch('/project/search', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        status: status,
        pageNum: 1,
        pageSize: 1  // 只需要 total 字段
      })
    });
    const data = await response.json();
    statistics[status] = data.data.total;
  }
  
  return statistics;
  // 返回：{ "进行中": 15, "已完成": 8, "已暂停": 3, "计划中": 5 }
}
```

---

## 测试用例

### 使用 cURL 测试

```bash
# 1. 按状态搜索
curl -X POST http://localhost:8080/project/search \
  -H "Content-Type: application/json" \
  -d '{"status":"进行中","pageNum":1,"pageSize":10}'

# 2. 按名称模糊搜索
curl -X POST http://localhost:8080/project/search \
  -H "Content-Type: application/json" \
  -d '{"name":"电商","pageNum":1,"pageSize":10}'

# 3. 组合搜索
curl -X POST http://localhost:8080/project/search \
  -H "Content-Type: application/json" \
  -d '{"status":"进行中","name":"电商","sortField":"create_time","sortOrder":"desc"}'

# 4. GET 方式按状态查询
curl -X GET "http://localhost:8080/project/status/进行中?page=1&pageSize=10"
```

### 使用 Postman 测试

1. **导入接口**
   - 访问 `http://localhost:8080/swagger-ui.html`
   - 查看项目管理相关的接口文档

2. **测试搜索接口**
   - URL: `POST http://localhost:8080/project/search`
   - Headers: `Content-Type: application/json`
   - Body (JSON):
     ```json
     {
       "status": "进行中",
       "pageNum": 1,
       "pageSize": 10
     }
     ```

---

## 性能优化建议

1. **索引优化**
   - 为 `status` 字段添加索引，提升状态搜索性能
   - 为 `name` 字段添加全文索引，优化模糊搜索

2. **分页限制**
   - 建议 `pageSize` 不超过 100
   - 避免查询过大的页码

3. **缓存策略**
   - 可对常用状态查询结果进行短时缓存（如 5 分钟）
   - 使用 Redis 缓存热门搜索条件

---

## 常见问题

### Q1: 如何搜索多个状态？

A: 目前接口支持单个状态查询。如需多状态查询，请分别调用接口或联系后端扩展功能。

### Q2: 状态名称大小写敏感吗？

A: 是的，状态查询是精确匹配，请确保状态名称完全一致。

### Q3: 搜索时所有条件都是必填的吗？

A: 不是。所有搜索条件都是可选的。如果不传任何条件，将返回所有项目（分页）。

### Q4: 如何实现实时搜索？

A: 建议使用防抖（debounce）技术，在用户输入停止 300-500ms 后再发起搜索请求。

```javascript
// 防抖实现
const debounce = (func, delay) => {
  let timer;
  return (...args) => {
    clearTimeout(timer);
    timer = setTimeout(() => func(...args), delay);
  };
};

// 使用
const debouncedSearch = debounce((keyword) => {
  // 调用搜索接口
  fetch('/project/search', { /* ... */ });
}, 300);
```

---

## 技术实现

### Service 层设计

所有查询功能统一通过 `ProjectService.getProjects(ProjectDTO dto)` 方法实现：

- **单一方法，多种用途**：通过传入不同的 DTO 参数实现不同的查询需求
- **灵活的参数处理**：DTO 为 null 时查询所有，否则按条件过滤
- **统一的分页排序**：所有查询都支持分页和排序功能

### Controller 层设计

提供 3 个接口，底层都调用同一个 `getProjects()` 方法：

1. `GET /project` - 查询所有（传入简单 DTO）
2. `POST /project/search` - 高级搜索（传入完整 DTO）
3. `GET /project/status/{status}` - 按状态查询（构建包含状态的 DTO）

**优势**：
- ✅ 代码复用，减少重复
- ✅ 维护简单，只需维护一个核心方法
- ✅ 逻辑统一，行为一致

---

## 更新日志

- **2024-11-28**: 新增项目搜索功能，支持状态、名称、ID 查询，统一使用 `getProjects()` 方法

