# include <stdio.h>

int main(void) {
  
  char* nomes[] = {"Lucas", "Pedro", "Felipe"}; 

  for (int i=0; i < sizeof(*nomes); ++i) {
    printf("Nome atual: %s\n", *nomes[i]);
  }
  
  

  return 0;
}
