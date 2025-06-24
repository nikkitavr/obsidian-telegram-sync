docker compose up -d

# Ждем, пока PostgreSQL будет готов
echo "⏳ Ожидание готовности PostgreSQL..."
until docker compose exec -T postgres pg_isready -U postgres; do
    echo "⏳ PostgreSQL еще не готов..."
    sleep 2
done

echo "✅ PostgreSQL успешно запущен!"