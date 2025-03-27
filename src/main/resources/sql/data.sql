INSERT INTO url (short_url, original_url, is_deleted, created_at, updated_at)
SELECT
    CONCAT(
        CHAR(97 + t1.n),  -- 첫 번째 자리 (a-z)
        CHAR(97 + t2.n),  -- 두 번째 자리 (a-z)
        CHAR(97 + t3.n),  -- 세 번째 자리 (a-z)
        CHAR(97 + t4.n)  -- 네 번째 자리 (a-z)
    ) AS short_url,
    'https://example.com/' || CONCAT(
        CHAR(97 + t1.n),
        CHAR(97 + t2.n),
        CHAR(97 + t3.n),
        CHAR(97 + t4.n)
    ),
    CASE WHEN RANDOM() < 0.2 THEN TRUE ELSE FALSE END,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM SYSTEM_RANGE(0, 25) AS t1(n)  -- a-z (0-25)
CROSS JOIN SYSTEM_RANGE(0, 25) AS t2(n)
CROSS JOIN SYSTEM_RANGE(0, 25) AS t3(n)
CROSS JOIN SYSTEM_RANGE(0, 25) AS t4(n)
LIMIT 50000000;  -- 1,000,000개로 제한 (필요 시 조정 가능)