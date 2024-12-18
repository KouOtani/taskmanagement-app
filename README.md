﻿# タスク管理アプリ

このプロジェクトは、タスクやプロジェクトを効果的に管理し、チームでの共同作業をサポートするアプリケーションです。ユーザーはタスクを管理し、進捗を把握しながらプロジェクトを効率的に進めることができます。

## 概要
このプロジェクトは、チームメンバーと連携しながらタスクを管理し、進捗を追跡するための完全なプロジェクト管理アプリケーションのプロトタイプです。<br>ユーザーはプロジェクトを作成し、タスクを追加し、チームメンバーとコミュニケーションを取ることができます。

## 主な機能
- **タスク管理**: タスクの追加、編集、削除が可能で、優先度や期限も設定できます。<br>


https://github.com/user-attachments/assets/9d94578e-92f3-4045-b5d7-949875fb4df1


- **プロジェクト作成・管理**: 新しいプロジェクトを作成し、概要や進行状況を管理できます。<br>


https://github.com/user-attachments/assets/dcf84c3a-9a0a-4dba-a745-21d6e60dfd5f


- **メンバー管理**: プロジェクトにメンバーを追加し、役割を割り当て、チームでの役割分担を明確化。<br>


https://github.com/user-attachments/assets/fbb9da74-5303-4220-b248-d28c9f8d9be6


- **チャット機能**: プロジェクトメンバーがリアルタイムでコミュニケーションできるチャット機能。<br>


https://github.com/user-attachments/assets/ff91beee-75de-4be3-be02-70193c07417d


- **通知機能**: メンバーに通知が届くため、チーム全体での情報共有が可能。<br>


https://github.com/user-attachments/assets/26b41837-2dba-49a9-931a-557581f4b094


- **タグ管理**: タスクやプロジェクトにタグを付けることで、検索やフィルタリングが容易に。<br>


https://github.com/user-attachments/assets/2282a719-1f6c-4897-93cc-8060456c1b16


## 技術スタック
### フロントエンド
![HTML5](https://img.shields.io/badge/HTML5-E34F26?logo=html5&logoColor=white&style=for-the-badge)
![CSS3](https://img.shields.io/badge/CSS3-1572B6?logo=css3&logoColor=white&style=for-the-badge)
![JavaScript](https://img.shields.io/badge/JavaScript-F7DF1E?logo=javascript&logoColor=black&style=for-the-badge)
![Bootstrap](https://img.shields.io/badge/Bootstrap-563D7C?logo=bootstrap&logoColor=white&style=for-the-badge)

### バックエンド
![Java](https://img.shields.io/badge/Java-007396?style=for-the-badge&logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?logo=spring-boot&logoColor=white&style=for-the-badge)
![MyBatis](https://img.shields.io/badge/MyBatis-CB3837?logo=mybatis&logoColor=white&style=for-the-badge)

### データベース
![MySQL](https://img.shields.io/badge/MySQL-4479A1?logo=mysql&logoColor=white&style=for-the-badge)

## 工夫した点

- **Spring Boot & MyBatisの組み合わせで効率的な開発**
  Spring BootをベースにMyBatisでデータベースアクセスを効率化し、簡潔でメンテナンス性の高い構造を実現しました。これにより、迅速な開発と拡張性の高いコードベースを実現しました。

- **通知機能によるスムーズなチームコミュニケーション**
  ロジェクト内でメンバーがコメントを追加すると、他のメンバーに通知が届く仕組みを導入。これにより、重要な情報がリアルタイムに共有され、コミュニケーションの円滑化が図られています。

- **リアルタイムチャット機能の追加**
  プロジェクトメンバーがリアルタイムで連絡を取り合えるチャット機能を実装し、タスクの進捗や課題を即座に共有できるようにしました。

- **ファイル添付機能**
  チャット内でプロジェクト関連ファイルを添付・共有できる機能を追加。必要な資料を手軽に共有することで、作業効率の向上に寄与しています。

- **柔軟なフィルタリングと検索機能**
  タスクのフィルタリング機能を追加し、優先度、ステータス、期限などに基づいた細かい検索が可能にしました。これにより、ユーザーは必要な情報に迅速にアクセスでき、作業効率が向上します。

- **タスクとプロジェクトのタグ付け**
  各タスクやプロジェクトにタグを設定できる機能を追加し、関連タスクやプロジェクトをすぐに見つけられるようにしました。これにより、ユーザーはプロジェクト全体の整理を効率よく行えるようになります。

- **個人タスク管理とプロジェクトタスク管理の統合**
  個人タスクとチームタスクを同一アプリ内で管理できるようにし、ユーザーは一つのインターフェースで全てのタスクを確認・管理できるようにしました。これにより、個人の仕事とチームでのタスクが視覚的に統合され、優先順位付けがしやすくなります。

- **ページネーション機能での効率的なデータ表示**
  大量のタスクやコメントがある場合に、ページネーションを適用して画面の読み込み時間を短縮。ユーザーは必要な情報をスムーズに確認でき、パフォーマンスが向上しました。
