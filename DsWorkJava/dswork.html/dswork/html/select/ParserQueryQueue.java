package dswork.html.select;

import dswork.html.parser.StringUtil;

/**
 * A character queue with parsing helpers.
 */
public class ParserQueryQueue
{
	private String queue;
	private int pos = 0;
	private static final char ESC = '\\'; // escape char for chomp balanced.

	/**
	 * Create a new TokenQueue.
	 * @param data string of data to back queue.
	 */
	public ParserQueryQueue(String data)
	{
		queue = data;
	}

	/**
	 * Is the queue empty?
	 * @return true if no data left in queue.
	 */
	public boolean isEmpty()
	{
		return remainingLength() == 0;
	}

	private int remainingLength()
	{
		return queue.length() - pos;
	}

	/**
	 * Retrieves but does not remove the first character from the queue.
	 * @return First character, or 0 if empty.
	 */
	public char peek()
	{
		return isEmpty() ? 0 : queue.charAt(pos);
	}

	/**
	 * Add a character to the start of the queue (will be the next character retrieved).
	 * @param c character to add
	 */
	public void addFirst(Character c)
	{
		addFirst(c.toString());
	}

	/**
	 * Add a string to the start of the queue.
	 * @param seq string to add.
	 */
	public void addFirst(String seq)
	{
		// not very performant, but an edge case
		queue = seq + queue.substring(pos);
		pos = 0;
	}

	/**
	 * Tests if the next characters on the queue match the sequence. Case insensitive.
	 * @param seq String to check queue for.
	 * @return true if the next characters match.
	 */
	public boolean matches(String seq)
	{
		return queue.regionMatches(true, pos, seq, 0, seq.length());
	}

	public boolean matchesCS(String seq)
	{
		return queue.startsWith(seq, pos);
	}

	public boolean matchesAny(String... seq)
	{
		for(String s : seq)
		{
			if(matches(s))
				return true;
		}
		return false;
	}

	public boolean matchesAny(char... seq)
	{
		if(isEmpty())
			return false;
		for(char c : seq)
		{
			if(queue.charAt(pos) == c)
				return true;
		}
		return false;
	}

	public boolean matchesStartTag()
	{
		return (remainingLength() >= 2 && queue.charAt(pos) == '<' && Character.isLetter(queue.charAt(pos + 1)));
	}

	public boolean matchChomp(String seq)
	{
		if(matches(seq))
		{
			pos += seq.length();
			return true;
		}
		else
		{
			return false;
		}
	}

	public boolean matchesWhitespace()
	{
		return !isEmpty() && StringUtil.isWhitespace(queue.charAt(pos));
	}

	public boolean matchesWord()
	{
		return !isEmpty() && Character.isLetterOrDigit(queue.charAt(pos));
	}

	public void advance()
	{
		if(!isEmpty())
			pos++;
	}

	public char consume()
	{
		return queue.charAt(pos++);
	}

	public void consume(String seq)
	{
		if(!matches(seq))
			throw new IllegalStateException("Queue did not match expected sequence");
		int len = seq.length();
		if(len > remainingLength())
			throw new IllegalStateException("Queue not long enough to consume sequence");
		pos += len;
	}

	public String consumeTo(String seq)
	{
		int offset = queue.indexOf(seq, pos);
		if(offset != -1)
		{
			String consumed = queue.substring(pos, offset);
			pos += consumed.length();
			return consumed;
		}
		else
		{
			return remainder();
		}
	}

	public String consumeToIgnoreCase(String seq)
	{
		int start = pos;
		String first = seq.substring(0, 1);
		boolean canScan = first.toLowerCase().equals(first.toUpperCase()); // if first is not cased, use index of
		while(!isEmpty())
		{
			if(matches(seq))
				break;
			if(canScan)
			{
				int skip = queue.indexOf(first, pos) - pos;
				if(skip == 0) // this char is the skip char, but not match, so force advance of pos
					pos++;
				else if(skip < 0) // no chance of finding, grab to end
					pos = queue.length();
				else
					pos += skip;
			}
			else
				pos++;
		}
		return queue.substring(start, pos);
	}

	public String consumeToAny(String... seq)
	{
		int start = pos;
		while(!isEmpty() && !matchesAny(seq))
		{
			pos++;
		}
		return queue.substring(start, pos);
	}

	public String chompTo(String seq)
	{
		String data = consumeTo(seq);
		matchChomp(seq);
		return data;
	}

	public String chompToIgnoreCase(String seq)
	{
		String data = consumeToIgnoreCase(seq); // case insensitive scan
		matchChomp(seq);
		return data;
	}

	public String chompBalanced(char open, char close)
	{
		int start = -1;
		int end = -1;
		int depth = 0;
		char last = 0;
		boolean inQuote = false;
		do
		{
			if(isEmpty())
				break;
			Character c = consume();
			if(last == 0 || last != ESC)
			{
				if((c.equals('\'') || c.equals('"')) && c != open)
					inQuote = !inQuote;
				if(inQuote)
					continue;
				if(c.equals(open))
				{
					depth++;
					if(start == -1)
						start = pos;
				}
				else if(c.equals(close))
					depth--;
			}
			if(depth > 0 && last != 0)
				end = pos; // don't include the outer match pair in the return
			last = c;
		}
		while(depth > 0);
		return (end >= 0) ? queue.substring(start, end) : "";
	}

	public static String unescape(String in)
	{
		StringBuilder out = new StringBuilder();
		char last = 0;
		for(char c : in.toCharArray())
		{
			if(c == ESC)
			{
				if(last != 0 && last == ESC)
					out.append(c);
			}
			else
				out.append(c);
			last = c;
		}
		return out.toString();
	}

	public boolean consumeWhitespace()
	{
		boolean seen = false;
		while(matchesWhitespace())
		{
			pos++;
			seen = true;
		}
		return seen;
	}

	public String consumeWord()
	{
		int start = pos;
		while(matchesWord())
			pos++;
		return queue.substring(start, pos);
	}

	public String consumeTagName()
	{
		int start = pos;
		while(!isEmpty() && (matchesWord() || matchesAny(':', '_', '-')))
			pos++;
		return queue.substring(start, pos);
	}

	public String consumeElementSelector()
	{
		int start = pos;
		while(!isEmpty() && (matchesWord() || matchesAny("*|", "|", "_", "-")))
			pos++;
		return queue.substring(start, pos);
	}

	public String consumeCssIdentifier()
	{
		int start = pos;
		while(!isEmpty() && (matchesWord() || matchesAny('-', '_')))
			pos++;
		return queue.substring(start, pos);
	}

	public String consumeAttributeKey()
	{
		int start = pos;
		while(!isEmpty() && (matchesWord() || matchesAny('-', '_', ':')))
			pos++;
		return queue.substring(start, pos);
	}

	public String remainder()
	{
		final String remainder = queue.substring(pos, queue.length());
		pos = queue.length();
		return remainder;
	}

	@Override
	public String toString()
	{
		return queue.substring(pos);
	}
}
