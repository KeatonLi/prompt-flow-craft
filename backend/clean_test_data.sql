DELETE FROM shared_prompt WHERE description LIKE '这是一个测试描述%' OR prompt_content LIKE '你是一个专业的%' OR description IS NULL OR TRIM(description) = '' OR TRIM(prompt_content) = '';
