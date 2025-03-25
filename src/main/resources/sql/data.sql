INSERT INTO url (short_url, original_url, is_deleted, created_at, updated_at)
SELECT
    CAST(n AS VARCHAR(255)),
    'https://example.com/' || n,  -- CONCAT 대신 || 연산자 사용 (H2 표준)
    CASE WHEN RANDOM() < 0.2 THEN TRUE ELSE FALSE END,  -- RAND() 대신 RANDOM()
    CURRENT_TIMESTAMP,  -- NOW() 대신 H2 표준
    CURRENT_TIMESTAMP
FROM SYSTEM_RANGE(1, 1000000) AS t(n);  -- 별칭을 명시적으로 지정