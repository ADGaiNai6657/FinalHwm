# FinalHwm

一个基于 Java Swing 实现的“跑得快”扑克牌桌面小游戏。项目包含登录界面、三人对局界面、牌型判断、电脑自动出牌、积分统计和重新开局等基础流程。

## 项目功能

- 登录验证：输入账号、默认密码和验证码后进入游戏。
- 三人对局：玩家与两名电脑玩家进行跑得快对局。
- 扑克牌显示：使用 `src/image/poker` 下的图片资源展示正面牌和背面牌。
- 牌型判断：支持单张、对子、三张、三带一、三带二、顺子、连对、飞机、炸弹、四带二等牌型。
- 出牌比较：根据上一手牌判断当前选择是否可以压过。
- 电脑出牌：电脑会自动寻找可出的最小牌型。
- 游戏流程：支持不出、重新开始、胜者先手和积分更新。

## 登录说明

- 账号：非空即可。
- 密码：`123456`
- 验证码：以登录界面右侧显示内容为准，点击验证码可以刷新。

## 运行环境

- JDK 8 或更高版本
- IntelliJ IDEA 或其他支持 Java Swing 项目的 IDE

## 运行方式

### 使用 IntelliJ IDEA

1. 使用 IntelliJ IDEA 打开项目根目录。
2. 确认 `src` 已被识别为 Sources Root。
3. 运行主类：

```text
RunFast.Core.Main
```

### 使用命令行

在项目根目录执行：

```powershell
Get-ChildItem -Recurse src -Filter *.java | ForEach-Object FullName > sources.txt
javac -encoding UTF-8 -d out @sources.txt
java -cp out RunFast.Core.Main
```

运行时请保持当前目录为项目根目录，程序会从 `src/image/poker` 加载扑克牌图片资源。

## 操作说明

- 点击手牌可以选中或取消选中。
- 点击“出牌”提交当前选择的牌。
- 当没有可压过上一手的牌时，可以点击“不出”。
- 点击“重新开始”可以开始新一局。
- 第一局由持有梅花 3 的玩家先出，之后由上一局胜者先出。

## 项目结构

```text
FinalHwm
├─ src
│  ├─ RunFast
│  │  ├─ Core
│  │  │  ├─ Main.java
│  │  │  ├─ Card.java
│  │  │  ├─ CardPattern.java
│  │  │  ├─ Judge.java
│  │  │  └─ PockerGame.java
│  │  └─ JFrame
│  │     ├─ LoginFrame.java
│  │     └─ GameJFrame.java
│  └─ image
│     ├─ login
│     └─ poker
└─ FinalHwm.iml
```

## 核心类说明

- `RunFast.Core.Main`：程序入口，启动登录窗口。
- `RunFast.JFrame.LoginFrame`：登录界面，负责账号、密码和验证码校验。
- `RunFast.JFrame.GameJFrame`：游戏主界面，负责发牌、出牌、回合推进和界面渲染。
- `RunFast.Core.Card`：扑克牌对象，负责牌面数据、牌值计算和图片显示。
- `RunFast.Core.CardPattern`：牌型结果对象，记录牌型、主牌大小、长度和张数。
- `RunFast.Core.Judge`：牌型判断和出牌比较工具类。

## 资源说明

扑克牌图片位于 `src/image/poker`，登录相关图片位于 `src/image/login`。如果移动或打包项目，需要确保这些资源可以被程序正确加载。
