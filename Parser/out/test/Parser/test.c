int main()
{
    printf("Hello world!");
    int n = 10;
    double sum = n;
    for (int i = 0; i < 20; i++)
    {
        if (i & 1 == 0)
            sum += i / 2;
    }
    printf("%d\n", sum);
    return 0;
}