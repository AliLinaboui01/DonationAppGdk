TEST_RES=$(curl -X "POST" "http://localhost:8080/genres" \
               -H 'Content-Type: application/json; charset=utf-8' \
               -d $'{ "name": "music" }')

echo "Result: $TEST_RES"