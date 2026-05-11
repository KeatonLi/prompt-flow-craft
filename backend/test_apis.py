#!/usr/bin/env python3
"""
API 集成测试脚本
测试所有后端接口，验证换行符是否正确返回
只测试前端实际使用的接口
"""

import requests
import json
import sys

BASE_URL = "http://111.231.107.210:8080/api"

def repr(s, max_len=200):
    """安全截断字符串"""
    if s is None:
        return "null"
    if len(s) <= max_len:
        return s
    return s[:max_len] + "..."

def test_health():
    """测试健康检查接口"""
    print("\n========== 测试 /api/health ==========")
    try:
        r = requests.get(f"{BASE_URL}/health", timeout=10)
        print(f"HTTP状态码: {r.status_code}")
        print(f"响应: {r.text}")
        assert r.status_code == 200
        print("✅ /api/health 测试通过\n")
        return True
    except Exception as e:
        print(f"❌ /api/health 测试失败: {e}\n")
        return False

def test_generate_agent_stream():
    """测试流式生成Agent提示词接口"""
    print("\n========== 测试 /api/generate-agent/stream (流式) ==========")
    try:
        data = {"name": "测试Agent", "roleDescription": "你是一个专业的AI助手"}
        full_response = []

        r = requests.post(f"{BASE_URL}/generate-agent/stream", json=data,
                          headers={"Accept": "text/event-stream"}, stream=True, timeout=120)
        print(f"HTTP状态码: {r.status_code}")

        event_name = ''
        event_data = ''

        for line in r.iter_lines():
            if line:
                line_str = line.decode('utf-8')

                if line_str.startswith('event:'):
                    if event_data:
                        full_response.append(event_data)
                        event_data = ''
                    event_name = line_str[6:].strip()
                elif line_str.startswith('data:'):
                    data_content = line_str[5:]
                    if data_content == '':
                        event_data += '\n'
                    else:
                        event_data += data_content
                elif line_str.strip() == '':
                    if event_data:
                        full_response.append(event_data)
                        event_data = ''
                        event_name = ''

        if event_data:
            full_response.append(event_data)

        complete = ''.join(full_response)
        newline_count = complete.count('\n')

        print(f"完整响应长度: {len(complete)}")
        print(f"换行符数量: {newline_count}")
        print(f"是否包含换行符: {newline_count > 0}")
        print(f"前200字符: {repr(complete, 200)}")

        assert r.status_code == 200
        assert len(complete) > 0
        print("✅ /api/generate-agent/stream 测试通过\n")
        return True
    except Exception as e:
        print(f"❌ /api/generate-agent/stream 测试失败: {e}\n")
        import traceback
        traceback.print_exc()
        return False

def test_generate_skill_stream():
    """测试流式生成Skill提示词接口"""
    print("\n========== 测试 /api/generate-skill/stream (流式) ==========")
    try:
        data = {"name": "天气查询", "description": "查询天气信息"}
        full_response = []

        r = requests.post(f"{BASE_URL}/generate-skill/stream", json=data,
                          headers={"Accept": "text/event-stream"}, stream=True, timeout=120)
        print(f"HTTP状态码: {r.status_code}")

        event_name = ''
        event_data = ''

        for line in r.iter_lines():
            if line:
                line_str = line.decode('utf-8')

                if line_str.startswith('event:'):
                    if event_data:
                        full_response.append(event_data)
                        event_data = ''
                    event_name = line_str[6:].strip()
                elif line_str.startswith('data:'):
                    data_content = line_str[5:]
                    if data_content == '':
                        event_data += '\n'
                    else:
                        event_data += data_content
                elif line_str.strip() == '':
                    if event_data:
                        full_response.append(event_data)
                        event_data = ''
                        event_name = ''

        if event_data:
            full_response.append(event_data)

        complete = ''.join(full_response)
        newline_count = complete.count('\n')

        print(f"完整响应长度: {len(complete)}")
        print(f"换行符数量: {newline_count}")
        print(f"是否包含换行符: {newline_count > 0}")
        print(f"前200字符: {repr(complete, 200)}")

        assert r.status_code == 200
        assert len(complete) > 0
        print("✅ /api/generate-skill/stream 测试通过\n")
        return True
    except Exception as e:
        print(f"❌ /api/generate-skill/stream 测试失败: {e}\n")
        import traceback
        traceback.print_exc()
        return False

def test_history_page():
    """测试历史记录分页接口"""
    print("\n========== 测试 /api/history/page ==========")
    try:
        r = requests.get(f"{BASE_URL}/history/page?page=1&size=10", timeout=10)
        print(f"HTTP状态码: {r.status_code}")
        resp = r.json()
        print(f"响应: success={resp.get('success')}, total={resp.get('total')}")
        assert r.status_code == 200
        assert resp.get('success') == True
        print("✅ /api/history/page 测试通过\n")
        return True
    except Exception as e:
        print(f"❌ /api/history/page 测试失败: {e}\n")
        return False

def test_share_list():
    """测试分享列表接口"""
    print("\n========== 测试 /api/share ==========")
    try:
        r = requests.get(f"{BASE_URL}/share", timeout=10)
        print(f"HTTP状态码: {r.status_code}")
        resp = r.json()
        print(f"响应: success={resp.get('success')}, total={resp.get('total')}")
        assert r.status_code == 200
        assert resp.get('success') == True
        print("✅ /api/share 测试通过\n")
        return True
    except Exception as e:
        print(f"❌ /api/share 测试失败: {e}\n")
        return False

def test_share_recent():
    """测试最近分享接口"""
    print("\n========== 测试 /api/share/recent ==========")
    try:
        r = requests.get(f"{BASE_URL}/share/recent?limit=5", timeout=10)
        print(f"HTTP状态码: {r.status_code}")
        resp = r.json()
        print(f"响应: success={resp.get('success')}")
        assert r.status_code == 200
        assert resp.get('success') == True
        print("✅ /api/share/recent 测试通过\n")
        return True
    except Exception as e:
        print(f"❌ /api/share/recent 测试失败: {e}\n")
        return False

def test_categories():
    """测试分类接口"""
    print("\n========== 测试 /api/categories ==========")
    try:
        r = requests.get(f"{BASE_URL}/categories", timeout=10)
        print(f"HTTP状态码: {r.status_code}")
        resp = r.json()
        print(f"响应: success={resp.get('success')}, 数据条数={len(resp.get('data', []))}")
        assert r.status_code == 200
        assert resp.get('success') == True
        print("✅ /api/categories 测试通过\n")
        return True
    except Exception as e:
        print(f"❌ /api/categories 测试失败: {e}\n")
        return False

def test_tags():
    """测试标签接口"""
    print("\n========== 测试 /api/tags ==========")
    try:
        r = requests.get(f"{BASE_URL}/tags", timeout=10)
        print(f"HTTP状态码: {r.status_code}")
        resp = r.json()
        print(f"响应: success={resp.get('success')}, 数据条数={len(resp.get('data', []))}")
        assert r.status_code == 200
        assert resp.get('success') == True
        print("✅ /api/tags 测试通过\n")
        return True
    except Exception as e:
        print(f"❌ /api/tags 测试失败: {e}\n")
        return False

def main():
    print("=" * 60)
    print("API 集成测试 - 测试前端实际使用的接口")
    print(f"服务器地址: {BASE_URL}")
    print("=" * 60)

    tests = [
        ("健康检查", test_health),
        ("流式生成Agent", test_generate_agent_stream),
        ("流式生成Skill", test_generate_skill_stream),
        ("历史记录分页", test_history_page),
        ("分享列表", test_share_list),
        ("最近分享", test_share_recent),
        ("分类", test_categories),
        ("标签", test_tags),
    ]

    results = []
    for name, test_func in tests:
        try:
            passed = test_func()
            results.append((name, passed))
        except Exception as e:
            print(f"❌ {name} 测试异常: {e}\n")
            results.append((name, False))

    # 打印汇总
    print("\n" + "=" * 60)
    print("测试结果汇总")
    print("=" * 60)
    passed_count = 0
    for name, passed in results:
        status = "✅ 通过" if passed else "❌ 失败"
        print(f"  {name}: {status}")
        if passed:
            passed_count += 1
    print(f"\n总计: {passed_count}/{len(results)} 通过")
    print("=" * 60)

    return 0 if passed_count == len(results) else 1

if __name__ == "__main__":
    sys.exit(main())
